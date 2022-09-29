package org.smoothbuild.compile.ps.infer;

import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.util.collect.Lists.map;
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
import org.smoothbuild.compile.ps.ast.refable.FuncP;
import org.smoothbuild.compile.ps.ast.refable.ItemP;
import org.smoothbuild.compile.ps.ast.refable.NamedValP;
import org.smoothbuild.compile.ps.ast.refable.RefableP;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.bindings.ScopedBindings;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.function.TriFunction;

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
      Bindings<? extends Optional<? extends RefableS>> outerBindings, Logger logger, NamedValP val) {
    return new TypeInferrer(types, outerBindings, logger)
        .inferValSchema(val);
  }

  private Optional<SchemaS> inferValSchema(NamedValP val) {
    return translateOrGenerateT(val.type())
        .flatMap(r -> unifyBodyAndResolve(val, r, this::resolveValSchema));
  }

  private Optional<SchemaS> resolveValSchema(NamedValP namedValP, TypeS evalT,
      Bindings<? extends Optional<? extends RefableS>> bindings) {
    return new TypeInferrerResolve(unifier, logger, bindings).resolve(namedValP, evalT);
  }

  // default arg

  private void inferDefaultArg(ItemP param) {
    new TypeInferrer(typePsTranslator, bindings, logger, new Unifier())
        .inferDefaultArgImpl(param.typeS(), param);
  }

  private void inferDefaultArgImpl(TypeS type, ItemP param) {
    unifyBodyAndResolve(param, type, this::resolveDefaultArg);
  }

  private Optional<Void> resolveDefaultArg(ItemP param, TypeS type,
      Bindings<? extends Optional<? extends RefableS>> bindings) {
    new TypeInferrerResolve(unifier, logger, bindings)
        .resolve(param);
    // This optional is ignored by caller.
    return Optional.empty();
  }

  // func

  public static Optional<FuncSchemaS> inferFuncSchema(Bindings<Optional<TDefS>> types,
      Bindings<? extends Optional<? extends RefableS>> outerBindings, Logger logger, FuncP func) {
    return new TypeInferrer(types, outerBindings, logger)
        .inferFuncSchema(func);
  }

  private Optional<FuncSchemaS> inferFuncSchema(FuncP func) {
    var params = func.params();
    if (!inferParamTs(params)) {
      return Optional.empty();
    }
    return inferFuncSchemaImpl(func, params);
  }

  private boolean inferParamTs(NList<ItemP> params) {
    for (var param : params) {
      var type = typePsTranslator.translate(param.type());
      if (type.isPresent()) {
        type.get().vars().forEach(unifier::addVar);
        param.setTypeS(type.get());
      } else {
        return false;
      }
    }
    return true;
  }

  private Optional<FuncSchemaS> inferFuncSchemaImpl(FuncP func, NList<ItemP> params) {
    params.stream()
        .filter(p -> p.body().isPresent())
        .forEach(this::inferDefaultArg);
    var resT = translateOrGenerateT(func.resT());

    return resT.flatMap(
        r -> funcBodyTypeInferrer(params).unifyBodyAndResolve(func, r, this::resolveFuncSchema));
  }

  private TypeInferrer funcBodyTypeInferrer(NList<ItemP> params) {
    return new TypeInferrer(typePsTranslator, funcBodyScopeBindings(params), logger, unifier);
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

  // body

  private <R extends RefableP, T> Optional<T> unifyBodyAndResolve(R refable, TypeS evalT,
      TriFunction<R, TypeS, Bindings<? extends Optional<? extends RefableS>>, Optional<T>> resolver) {
    if (refable.body().isPresent()) {
      return new ExprTypeUnifier(unifier, bindings, logger)
          .unifyExpr(refable.body().get())
          .flatMap(bodyT -> unifyBodyWithEvalAndResolve(refable, evalT, bodyT, resolver));
    } else {
      return resolver.apply(refable, evalT, bindings);
    }
  }

  private <R extends RefableP, T> Optional<T> unifyBodyWithEvalAndResolve(
      R refable, TypeS evalT, TypeS bodyT,
      TriFunction<R, TypeS, Bindings<? extends Optional<? extends RefableS>>, Optional<T>> resolver) {
    try {
      unifier.unify(evalT, bodyT);
      return resolver.apply(refable, evalT, bindings);
    } catch (UnifierExc e) {
      logger.log(compileError(
          refable.loc(), refable.q() + " body type is not equal to declared type."));
      return Optional.empty();
    }
  }

  // helpers

  private Optional<TypeS> translateOrGenerateT(Optional<TypeP> typeP) {
    return typeP.map(this::translateT)
        .orElseGet(() -> Optional.of(unifier.generateUniqueVar()));
  }

  private Optional<TypeS> translateT(TypeP type) {
    var evalT = typePsTranslator.translate(type);
    evalT.ifPresent(typeS -> typeS.vars().forEach(unifier::addVar));
    return evalT;
  }
}
