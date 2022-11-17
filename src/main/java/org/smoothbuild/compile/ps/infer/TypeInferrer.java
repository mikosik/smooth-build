package org.smoothbuild.compile.ps.infer;

import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.toMap;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.Optional;

import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.lang.define.ItemSigS;
import org.smoothbuild.compile.lang.define.RefableS;
import org.smoothbuild.compile.lang.define.TDefS;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.tool.Unifier;
import org.smoothbuild.compile.lang.type.tool.UnifierExc;
import org.smoothbuild.compile.ps.ast.StructP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.refable.EvaluableP;
import org.smoothbuild.compile.ps.ast.refable.FuncP;
import org.smoothbuild.compile.ps.ast.refable.ItemP;
import org.smoothbuild.compile.ps.ast.refable.NamedValueP;
import org.smoothbuild.compile.ps.ast.refable.RefableP;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.bindings.ScopedBindings;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.function.TriFunction;

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

  // val

  public static Optional<SchemaS> inferValSchema(Bindings<Optional<TDefS>> types,
      Bindings<? extends Optional<? extends RefableS>> outerBindings, Logger logger, NamedValueP val) {
    return new TypeInferrer(types, outerBindings, logger)
        .inferValSchema(val);
  }

  private Optional<SchemaS> inferValSchema(NamedValueP val) {
    return translateOrGenerateTempVar(val.type())
        .flatMap(r -> unifyBodyAndResolve(val, r, this::resolveValSchema));
  }

  private Optional<SchemaS> resolveValSchema(NamedValueP namedValueP, TypeS evalT,
      Bindings<? extends Optional<? extends RefableS>> bindings) {
    return new TypeInferrerResolve(unifier, logger, bindings).resolve(namedValueP, evalT);
  }

  // func

  public static Optional<FuncSchemaS> inferFuncSchema(Bindings<Optional<TDefS>> types,
      Bindings<? extends Optional<? extends RefableS>> outerBindings, Logger logger, FuncP func) {
    return new TypeInferrer(types, outerBindings, logger)
        .inferFuncSchema(func);
  }

  private Optional<FuncSchemaS> inferFuncSchema(FuncP func) {
    var params = func.params();
    if (!inferParamDefaultValues(params)) {
      return Optional.empty();
    }
    if (!inferParamTs(params)) {
      return Optional.empty();
    }
    var resT = translateOrGenerateTempVar(func.resT());
    var funcSchemaS = resT.flatMap(
        r -> funcBodyTypeInferrer(params).unifyBodyAndResolve(func, r, this::resolveFuncSchema));
    funcSchemaS.ifPresent(schema -> detectTypeErrorsBetweenParamAndItsDefaultValue(schema, params));
    return funcSchemaS;
  }

  private boolean inferParamTs(NList<ItemP> params) {
    for (var param : params) {
      var type = typePsTranslator.translate(param.type());
      if (type.isPresent()) {
        param.setTypeS(type.get());
      } else {
        return false;
      }
    }
    return true;
  }

  private TypeInferrer funcBodyTypeInferrer(NList<ItemP> params) {
    return new TypeInferrer(typePsTranslator, funcBodyScopeBindings(params), logger, unifier);
  }

  private void detectTypeErrorsBetweenParamAndItsDefaultValue(
      FuncSchemaS resolvedFuncSchema, NList<ItemP> params) {
    var resolvedParamTs = resolvedFuncSchema.type().params().items();
    var paramUnifier = new Unifier();
    var paramMapping = toMap(resolvedFuncSchema.quantifiedVars(), v -> paramUnifier.newTempVar());
    for (int i = 0; i < params.size(); i++) {
      var resolvedParamT = resolvedParamTs.get(i);
      var param = params.get(i);
      param.defaultVal().ifPresent(body -> {
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

  private ScopedBindings<Optional<? extends RefableS>> funcBodyScopeBindings(NList<ItemP> params) {
    var bodyScopeBindings = new ScopedBindings<Optional<? extends RefableS>>(bindings);
    params.forEach(p -> bodyScopeBindings.add(p.name(), Optional.of(itemS(p))));
    return bodyScopeBindings;
  }

  private static ItemS itemS(ItemP p) {
    return new ItemS(p.typeS(), p.name(), Optional.empty(), p.loc());
  }

  private Optional<FuncSchemaS> resolveFuncSchema(FuncP funcP, TypeS resT,
      Bindings<? extends Optional<? extends RefableS>> bindings) {
    return new TypeInferrerResolve(unifier, logger, bindings)
        .resolve(funcP, new FuncTS(resT, funcP.paramTs()));
  }

  // param default value

  private boolean inferParamDefaultValues(NList<ItemP> params) {
    return params.stream()
        .flatMap(p -> p.defaultVal().stream())
        .allMatch(this::inferParamDefaultValue);
  }

  private boolean inferParamDefaultValue(ExprP body) {
    return new TypeInferrer(typePsTranslator, bindings, logger, new Unifier())
        .inferParamDefaultValueImpl(body);
  }

  private boolean inferParamDefaultValueImpl(ExprP body) {
    return new ExprTypeUnifier(unifier, bindings, logger)
        .unifyExpr(body)
        .map(t -> resolveParamDefaultValue(body))
        .orElse(false);
  }

  private boolean resolveParamDefaultValue(ExprP body) {
    return new TypeInferrerResolve(unifier, logger, bindings)
        .resolveParamDefaultValue(body);
  }

  // body

  private <R extends EvaluableP, T> Optional<T> unifyBodyAndResolve(R evaluable, TypeS evalT,
      TriFunction<R, TypeS, Bindings<? extends Optional<? extends RefableS>>, Optional<T>> resolver) {
    if (evaluable.body().isPresent()) {
      return new ExprTypeUnifier(unifier, bindings, logger)
          .unifyExpr(evaluable.body().get())
          .flatMap(bodyT -> unifyBodyWithEvalTypeAndResolve(evaluable, evalT, bodyT, resolver));
    } else {
      return resolver.apply(evaluable, evalT, bindings);
    }
  }

  private <R extends RefableP, T> Optional<T> unifyBodyWithEvalTypeAndResolve(
      R evaluable, TypeS evalT, TypeS bodyT,
      TriFunction<R, TypeS, Bindings<? extends Optional<? extends RefableS>>, Optional<T>> resolver) {
    try {
      unifier.unify(evalT, bodyT);
      return resolver.apply(evaluable, evalT, bindings);
    } catch (UnifierExc e) {
      logger.log(compileError(
          evaluable.loc(), evaluable.q() + " body type is not equal to declared type."));
      return Optional.empty();
    }
  }

  // helpers

  private Optional<TypeS> translateOrGenerateTempVar(Optional<TypeP> typeP) {
    return typeP.map(typePsTranslator::translate)
        .orElseGet(() -> Optional.of(unifier.newTempVar()));
  }
}
