package org.smoothbuild.compile.ps.infer;

import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.toMap;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.Optional;

import org.smoothbuild.compile.lang.define.ItemSigS;
import org.smoothbuild.compile.lang.define.RefableS;
import org.smoothbuild.compile.lang.define.TDefS;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.tool.Unifier;
import org.smoothbuild.compile.lang.type.tool.UnifierExc;
import org.smoothbuild.compile.ps.ast.StructP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.refable.ItemP;
import org.smoothbuild.compile.ps.ast.refable.NamedFuncP;
import org.smoothbuild.compile.ps.ast.refable.NamedValueP;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.collect.NList;

/**
 * Type inferring consists of
 *   - replacing not declared types with TempVar-s
 *   - walking expression tree converting types to normalized
 *       and unifying them {@link ExprTypeUnifier}
 *   - inferring unit types {@link UnitTypeInferrer}
 *   - resolving types from normalized {@link TypeInferrerResolve}
 */
public class TypeInferrer {
  private final TypePsTranslator typePsTranslator;
  private final Bindings<? extends Optional<? extends RefableS>> bindings;
  private final Logger logger;
  private final Unifier unifier;

  public TypeInferrer(Bindings<Optional<TDefS>> types,
      Bindings<? extends Optional<? extends RefableS>> bindings, Logger logger) {
    this(new TypePsTranslator(types), bindings, logger, new Unifier());
  }

  public TypeInferrer(TypePsTranslator typePsTranslator,
      Bindings<? extends Optional<? extends RefableS>> bindings, Logger logger, Unifier unifier) {
    this.typePsTranslator = typePsTranslator;
    this.bindings = bindings;
    this.logger = logger;
    this.unifier = unifier;
  }

  public static Optional<StructTS> inferStructType(Bindings<Optional<TDefS>> types,
      Bindings<? extends Optional<? extends RefableS>> outerBindings, Logger logger,
      StructP struct) {
    return new TypeInferrer(types, outerBindings, logger)
        .inferStructT(struct);
  }

  private Optional<StructTS> inferStructT(StructP struct) {
    return pullUp(map(struct.fields().list(), this::inferFieldSig))
        .map(NList::nlist)
        .map(is -> new StructTS(struct.name(), is));
  }

  private Optional<ItemSigS> inferFieldSig(ItemP field) {
    return typePsTranslator.translate(field.type())
        .flatMap(t -> {
          if (t.vars().isEmpty()) {
            return Optional.of(new ItemSigS(t, field.name()));
          } else {
            var message = "Field type cannot be polymorphic. Found field %s with type %s."
                .formatted(field.q(), t.q());
            logger.log(compileError(field.type(), message));
            return Optional.empty();
          }
        });
  }

  // value

  public Optional<SchemaS> inferValueSchema(NamedValueP namedValue) {
    if (unifyNamedValue(namedValue)) {
      return resolveValueSchema(namedValue);
    } else {
      return Optional.empty();
    }
  }

  private boolean unifyNamedValue(NamedValueP namedValue) {
    return new ExprTypeUnifier(unifier, typePsTranslator, bindings, logger)
        .unifyNamedValue(namedValue);
  }

  private Optional<SchemaS> resolveValueSchema(NamedValueP namedValueP) {
    return new TypeInferrerResolve(unifier, logger, bindings)
        .resolveNamedValue(namedValueP);
  }

  // func

  public Optional<FuncSchemaS> inferFuncSchema(NamedFuncP namedFunc) {
    var params = namedFunc.params();
    if (inferParamDefaultValues(params) && unifyNamedFunc(namedFunc)) {
      var funcSchemaS = resolveNamedFunc(namedFunc);
      funcSchemaS.ifPresent(s -> detectTypeErrorsBetweenParamAndItsDefaultValue(s, params));
      return funcSchemaS;
    }
    return Optional.empty();
  }

  private boolean unifyNamedFunc(NamedFuncP namedFunc) {
    return new ExprTypeUnifier(unifier, typePsTranslator, bindings, logger)
        .unifyNamedFunc(namedFunc);
  }

  private Optional<FuncSchemaS> resolveNamedFunc(NamedFuncP namedFunc) {
    return new TypeInferrerResolve(unifier, logger, bindings)
        .resolveNamedFunc(namedFunc);
  }

  private void detectTypeErrorsBetweenParamAndItsDefaultValue(
      FuncSchemaS resolvedFuncSchema, NList<ItemP> params) {
    var resolvedParamTs = resolvedFuncSchema.type().params().items();
    var paramUnifier = new Unifier();
    var paramMapping = toMap(resolvedFuncSchema.quantifiedVars(), v -> paramUnifier.newTempVar());
    for (int i = 0; i < params.size(); i++) {
      var resolvedParamT = resolvedParamTs.get(i);
      var param = params.get(i);
      param.defaultValue().ifPresent(body -> {
        var paramT = resolvedParamT.mapVars(paramMapping);
        var initializerMapping = toMap(body.typeS().vars(), v -> paramUnifier.newTempVar());
        var bodyT = body.typeS().mapVars(initializerMapping);
        try {
          paramUnifier.unify(paramT, bodyT);
        } catch (UnifierExc e) {
          var message = "Parameter %s has type %s so it cannot have default value with type %s."
                  .formatted(param.q(), resolvedParamT.q(), body.typeS().q());
          this.logger.log(compileError(body.loc(), message));
        }
      });
    }
  }

  // param default value

  private boolean inferParamDefaultValues(NList<ItemP> params) {
    return params.stream()
        .flatMap(p -> p.defaultValue().stream())
        .allMatch(this::inferParamDefaultValue);
  }

  private boolean inferParamDefaultValue(ExprP body) {
    return new TypeInferrer(typePsTranslator, bindings, logger, new Unifier())
        .inferParamDefaultValueImpl(body);
  }

  private boolean inferParamDefaultValueImpl(ExprP body) {
    return new ExprTypeUnifier(unifier, typePsTranslator, bindings, logger)
        .unifyExpr(body)
        .map(t -> resolveParamDefaultValue(body))
        .orElse(false);
  }

  private boolean resolveParamDefaultValue(ExprP body) {
    return new TypeInferrerResolve(unifier, logger, bindings)
        .resolveParamDefaultValue(body);
  }

  // body

  // helpers

  private Optional<TypeS> translateOrGenerateTempVar(Optional<TypeP> typeP) {
    return typeP.map(typePsTranslator::translate)
        .orElseGet(() -> Optional.of(unifier.newTempVar()));
  }
}
