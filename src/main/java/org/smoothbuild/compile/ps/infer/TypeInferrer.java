package org.smoothbuild.compile.ps.infer;

import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.toMap;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.Optional;

import org.smoothbuild.compile.lang.define.ItemSigS;
import org.smoothbuild.compile.lang.define.RefableS;
import org.smoothbuild.compile.lang.define.TDefS;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarSetS;
import org.smoothbuild.compile.lang.type.tool.Unifier;
import org.smoothbuild.compile.lang.type.tool.UnifierExc;
import org.smoothbuild.compile.ps.ast.StructP;
import org.smoothbuild.compile.ps.ast.refable.ItemP;
import org.smoothbuild.compile.ps.ast.refable.NamedFuncP;
import org.smoothbuild.compile.ps.ast.refable.NamedValueP;
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
  private final Unifier unifier;
  private final TypePsTranslator typePsTranslator;
  private final Bindings<? extends Optional<? extends RefableS>> bindings;
  private final Logger logger;

  public TypeInferrer(
      Bindings<Optional<TDefS>> types,
      Bindings<? extends Optional<? extends RefableS>> bindings,
      Logger logger) {
    this(new Unifier(), new TypePsTranslator(types), bindings, logger);
  }

  public TypeInferrer(
      Unifier unifier,
      TypePsTranslator typePsTranslator,
      Bindings<? extends Optional<? extends RefableS>> bindings,
      Logger logger) {
    this.unifier = unifier;
    this.typePsTranslator = typePsTranslator;
    this.bindings = bindings;
    this.logger = logger;
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

  public boolean inferNamedValueSchema(NamedValueP namedValue) {
    if (unifyNamedValue(namedValue)) {
      nameImplicitVars(namedValue);
      return resolveValueSchema(namedValue);
    } else {
      return false;
    }
  }

  private boolean unifyNamedValue(NamedValueP namedValue) {
    return new ExprTypeUnifier(unifier, typePsTranslator, bindings, logger)
        .unifyNamedValue(namedValue);
  }

  private void nameImplicitVars(NamedValueP namedValue) {
    new TempVarsNamer(unifier)
        .nameVarsInNamedValue(namedValue);
  }

  private boolean resolveValueSchema(NamedValueP namedValueP) {
    return new TypeInferrerResolve(unifier, logger)
        .resolveNamedValue(namedValueP);
  }

  // func

  public boolean inferNamedFuncSchema(NamedFuncP namedFunc) {
    var params = namedFunc.params();
    if (inferParamDefaultValues(params) && unifyNamedFunc(namedFunc)) {
      nameImplicitVars(namedFunc);
      if (resolveNamedFunc(namedFunc)) {
        detectTypeErrorsBetweenParamAndItsDefaultValue(namedFunc);
        return true;
      }
    }
    return false;
  }

  private boolean unifyNamedFunc(NamedFuncP namedFunc) {
    return new ExprTypeUnifier(unifier, typePsTranslator, bindings, logger)
        .unifyNamedFunc(namedFunc);
  }

  private void nameImplicitVars(NamedFuncP namedFunc) {
    new TempVarsNamer(unifier)
        .nameVarsInNamedFunc(namedFunc);
  }

  private boolean resolveNamedFunc(NamedFuncP namedFunc) {
    return new TypeInferrerResolve(unifier, logger)
        .resolveNamedFunc(namedFunc);
  }

  private void detectTypeErrorsBetweenParamAndItsDefaultValue(NamedFuncP namedFunc) {
    var params = namedFunc.params();
    for (int i = 0; i < params.size(); i++) {
      var param = params.get(i);
      var index = i;
      param.defaultValue().ifPresent(defaultvalue -> {
        var schema = namedFunc.schemaS();
        var paramUnifier = new Unifier();
        var resolvedParamT = schema.type().params().items().get(index);
        var paramT = replaceVarsWithTempVars(schema.quantifiedVars(), resolvedParamT, paramUnifier);
        var defaultValueType = replaceQuantifiedVarsWithTempVars(
            defaultvalue.schemaS(), paramUnifier);
        try {
          paramUnifier.unify(paramT, defaultValueType);
        } catch (UnifierExc e) {
          var message = "Parameter %s has type %s so it cannot have default value with type %s."
                  .formatted(param.q(), resolvedParamT.q(), defaultvalue.schemaS().type().q());
          this.logger.log(compileError(defaultvalue.loc(), message));
        }
      });
    }
  }

  private static TypeS replaceQuantifiedVarsWithTempVars(SchemaS schemaS, Unifier unifier) {
    return replaceVarsWithTempVars(schemaS.quantifiedVars(), schemaS.type(), unifier);
  }

  private static TypeS replaceVarsWithTempVars(VarSetS vars, TypeS type, Unifier unifier) {
    var mapping = toMap(vars, v -> unifier.newTempVar());
    return type.mapVars(mapping);
  }

  // param default value

  private boolean inferParamDefaultValues(NList<ItemP> params) {
    return params.stream()
        .flatMap(p -> p.defaultValue().stream())
        .allMatch(this::inferParamDefaultValue);
  }

  private boolean inferParamDefaultValue(NamedValueP defaultValue) {
    return new TypeInferrer(new Unifier(), typePsTranslator, bindings, logger)
        .inferNamedValueSchema(defaultValue);
  }
}
