package org.smoothbuild.compile.ps.infer;

import static com.google.common.collect.Maps.toMap;
import static org.smoothbuild.compile.lang.type.TypeFS.BLOB;
import static org.smoothbuild.compile.lang.type.TypeFS.INT;
import static org.smoothbuild.compile.lang.type.TypeFS.STRING;
import static org.smoothbuild.compile.lang.type.VarSetS.varSetS;
import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.compile.ps.infer.BindingsHelper.funcBodyScopeBindings;
import static org.smoothbuild.compile.ps.infer.InferPositionedArgs.inferPositionedArgs;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.zip;
import static org.smoothbuild.util.collect.Optionals.flatMapPair;
import static org.smoothbuild.util.collect.Optionals.mapPair;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.Optional;
import java.util.function.Function;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.lang.define.NamedFuncS;
import org.smoothbuild.compile.lang.define.RefableS;
import org.smoothbuild.compile.lang.type.ArrayTS;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarSetS;
import org.smoothbuild.compile.lang.type.tool.Unifier;
import org.smoothbuild.compile.lang.type.tool.UnifierExc;
import org.smoothbuild.compile.ps.ast.expr.AnonFuncP;
import org.smoothbuild.compile.ps.ast.expr.BlobP;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.IntP;
import org.smoothbuild.compile.ps.ast.expr.MonoizableP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.OrderP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.SelectP;
import org.smoothbuild.compile.ps.ast.expr.StringP;
import org.smoothbuild.compile.ps.ast.refable.EvaluableP;
import org.smoothbuild.compile.ps.ast.refable.FuncP;
import org.smoothbuild.compile.ps.ast.refable.ItemP;
import org.smoothbuild.compile.ps.ast.refable.NamedFuncP;
import org.smoothbuild.compile.ps.ast.refable.NamedValueP;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ExprTypeUnifier {
  private final Unifier unifier;
  private final TypePsTranslator typePsTranslator;
  private final Bindings<? extends Optional<? extends RefableS>> bindings;
  private final VarSetS outerScopeVars;
  private final Logger logger;

  public ExprTypeUnifier(Unifier unifier, TypePsTranslator typePsTranslator,
      Bindings<? extends Optional<? extends RefableS>> bindings, Logger logger) {
    this(unifier, typePsTranslator, bindings, varSetS(), logger);
  }

  public ExprTypeUnifier(Unifier unifier, TypePsTranslator typePsTranslator,
      Bindings<? extends Optional<? extends RefableS>> bindings, VarSetS outerScopeVars,
      Logger logger) {
    this.unifier = unifier;
    this.typePsTranslator = typePsTranslator;
    this.bindings = bindings;
    this.outerScopeVars = outerScopeVars;
    this.logger = logger;
  }

  public boolean unifyNamedFunc(NamedFuncP namedFunc) {
    return unifyFunc(namedFunc);
  }

  private boolean unifyFunc(FuncP func) {
    var paramTs = inferParamTs(func.params());
    var resT = translateOrGenerateTempVar(func.resT());
    return mapPair(paramTs, resT, (p, r) -> unifyFunc(func, p, r))
        .orElse(false);
  }

  private boolean unifyFunc(FuncP func, ImmutableList<TypeS> paramTs, TypeS resT) {
    var paramsS = func.params().map(ExprTypeUnifier::itemS);
    var bodyBindings = funcBodyScopeBindings(bindings, paramsS);
    var funcTS = new FuncTS(paramTs, resT);
    func.setTypeS(funcTS);
    return unifyEvaluableBody(func, resT, funcTS, bodyBindings);
  }

  private static ItemS itemS(ItemP p) {
    return new ItemS(p.typeS(), p.name(), Optional.empty(), p.loc());
  }

  private Optional<ImmutableList<TypeS>> inferParamTs(NList<ItemP> params) {
    var paramTs = pullUp(map(params, p -> typePsTranslator.translate(p.type())));
    paramTs.ifPresent(types -> zip(params, types, ItemP::setTypeS));
    return paramTs;
  }

  public boolean unifyNamedValue(NamedValueP namedValue) {
    return translateOrGenerateTempVar((namedValue).evalT())
        .map(evalT -> {
          namedValue.setTypeS(evalT);
          return unifyEvaluableBody(namedValue, evalT, evalT, bindings);
        })
        .orElse(false);
  }

  private Boolean unifyEvaluableBody(EvaluableP evaluable, TypeS evalT, TypeS type,
      Bindings<? extends Optional<? extends RefableS>> bindings) {
    var vars = outerScopeVars.withAdded(type.vars());
    return new ExprTypeUnifier(unifier, typePsTranslator, bindings, vars, logger)
        .unifyEvaluableBody(evaluable, evalT, type);
  }

  private Boolean unifyEvaluableBody(EvaluableP evaluable, TypeS evalT, TypeS type) {
    return evaluable.body()
        .map(body -> unifyBodyExprAndEvaluationType(evaluable, evalT, body))
        .orElse(true);
  }

  private boolean unifyBodyExprAndEvaluationType(EvaluableP evaluable, TypeS typeS, ExprP body) {
    return unifyExpr(body)
        .map(bodyT -> unifyEvaluationTypeWithBodyType(evaluable, typeS, bodyT))
        .orElse(false);
  }

  private boolean unifyEvaluationTypeWithBodyType(EvaluableP evaluable, TypeS typeS, TypeS bodyT) {
    try {
      unifier.unify(typeS, bodyT);
      return true;
    } catch (UnifierExc e) {
      logger.log(compileError(
          evaluable.loc(), evaluable.q() + " body type is not equal to declared type."));
      return false;
    }
  }

  public boolean unifyParamDefaultValue(ExprP defaultValue) {
    return unifyExpr(defaultValue).isPresent();
  }

  private Optional<TypeS> unifyExpr(ExprP expr) {
    // @formatter:off
    return switch (expr) {
      case CallP       callP       -> unifyAndMemoize(this::unifyCall, callP);
      case AnonFuncP   anonFuncP   -> unifyAndMemoize(this::unifyAnonFunc, anonFuncP);
      case NamedArgP   namedArgP   -> unifyAndMemoize(this::unifyNamedArg, namedArgP);
      case OrderP      orderP      -> unifyAndMemoize(this::unifyOrder, orderP);
      case RefP        refP        -> unifyAndMemoize(this::unifyRef, refP);
      case SelectP     selectP     -> unifyAndMemoize(this::unifySelect, selectP);
      case StringP     stringP     -> setAndMemoize(STRING, stringP);
      case IntP        intP        -> setAndMemoize(INT, intP);
      case BlobP       blobP       -> setAndMemoize(BLOB, blobP);
    };
    // @formatter:on
  }

  private Optional<TypeS> setAndMemoize(TypeS typeS, ExprP exprP) {
    exprP.setTypeS(typeS);
    return Optional.of(typeS);
  }

  private <T extends ExprP> Optional<TypeS> unifyAndMemoize(
      Function<T, Optional<TypeS>> unifier, T exprP) {
    var type = unifier.apply(exprP);
    type.ifPresent(exprP::setTypeS);
    return type;
  }

  private Optional<TypeS> unifyCall(CallP call) {
    var calleeT = unifyExpr(call.callee());
    var positionedArgs = positionedArgs(call);
    call.setPositionedArgs(positionedArgs);
    if (positionedArgs.isPresent()) {
      var argTs = pullUp(map(positionedArgs.get(), this::unifyExpr));
      return flatMapPair(calleeT, argTs, (c, a) -> unifyCall(c, a, call.loc()));
    } else {
      map(call.args(), this::unifyExpr);
      return Optional.empty();
    }
  }

  private Optional<ImmutableList<ExprP>> positionedArgs(CallP callP) {
    if (callP.callee() instanceof RefP refP) {
      return bindings.get(refP.name())
          .flatMap(refableS -> inferPositionedArgs(callP, refableParams(refableS), logger));
    } else {
      return inferPositionedArgs(callP, Optional.empty(), logger);
    }
  }

  private static Optional<NList<ItemS>> refableParams(RefableS refableS) {
    if (refableS instanceof NamedFuncS namedFuncS) {
      return Optional.of(namedFuncS.params());
    } else {
      return Optional.empty();
    }
  }

  private Optional<TypeS> unifyCall(TypeS calleeT, ImmutableList<TypeS> argTs, Loc loc) {
    var resT = unifier.newTempVar();
    var funcT = new FuncTS(argTs, resT);
    try {
      unifier.unify(funcT, calleeT);
      return Optional.of(resT);
    } catch (UnifierExc e) {
      logger.log(compileError(loc, "Illegal call."));
      return Optional.empty();
    }
  }

  private Optional<TypeS> unifyAnonFunc(AnonFuncP anonFuncP) {
    if (unifyFunc(anonFuncP)) {
      // `type` needs to be resolved in case its resolved version contains variables
      // that are quantified in outer scope, so we won't include them in quantifiedVars below.
      var resolvedFuncT = (FuncTS) unifier.resolve(anonFuncP.typeS());
      var resolvedQuantifiedVars = resolvedFuncT.vars()
          .withRemoved(outerScopeVars.map(unifier::resolve));
      var funcSchemaS = new FuncSchemaS(resolvedQuantifiedVars, resolvedFuncT);
      anonFuncP.setSchemaS(funcSchemaS);
      return unifyMonoizable(anonFuncP);
    } else {
      return Optional.empty();
    }
  }

  private Optional<TypeS> unifyNamedArg(NamedArgP namedArgP) {
    return unifyExpr(namedArgP.expr());
  }

  private Optional<TypeS> unifyOrder(OrderP order) {
    var elems = order.elems();
    var elemTs = pullUp(map(elems, this::unifyExpr));
    return elemTs.flatMap(types -> unifyElemsWithArray(types, order.loc()));
  }

  private Optional<TypeS> unifyElemsWithArray(ImmutableList<TypeS> elemTs, Loc loc) {
    var elemVar = unifier.newTempVar();
    for (TypeS elemT : elemTs) {
      try {
        unifier.unify(elemVar, elemT);
      } catch (UnifierExc e) {
        logger.log(compileError(
            loc, "Cannot infer type for array literal. Its element types are not compatible."));
        return Optional.empty();
      }
    }
    return Optional.of(new ArrayTS(elemVar));
  }

  private Optional<TypeS> unifyRef(RefP ref) {
    var refable = bindings.get(ref.name());
    return refable.flatMap(r -> unifyRef(ref, r));
  }

  private Optional<? extends TypeS> unifyRef(RefP ref, RefableS refable) {
    return switch (refable) {
      case ItemS item -> unifyItemRef(ref, item);
      case NamedEvaluableS evaluable -> unifyEvaluableRef(ref, evaluable);
    };
  }

  private static Optional<TypeS> unifyItemRef(RefP ref, ItemS item) {
    ref.setMonoizeVarMap(ImmutableMap.of());
    return Optional.of(item.type());
  }

  private Optional<TypeS> unifyEvaluableRef(RefP ref, NamedEvaluableS evaluable) {
    ref.setSchemaS(evaluable.schema());
    return unifyMonoizable(ref);
  }

  private Optional<TypeS> unifyMonoizable(MonoizableP monoizableP) {
    var schema = monoizableP.schemaS();
    var varMap = toMap(schema.quantifiedVars(), v -> (TypeS) unifier.newTempVar());
    monoizableP.setMonoizeVarMap(varMap);
    return Optional.of(schema.monoize(monoizableP.monoizeVarMap()));
  }

  private Optional<TypeS> unifySelect(SelectP select) {
    var selectableT = unifyExpr(select.selectable());
    return selectableT.flatMap(t -> {
      if (unifier.resolve(t) instanceof StructTS structTS) {
        var itemSigS = structTS.fields().get(select.field());
        if (itemSigS == null) {
          logger.log(compileError(select.loc(), "Unknown field `" + select.field() + "`."));
          return Optional.empty();
        } else {
          return Optional.of(itemSigS.type());
        }
      } else {
        logger.log(compileError(select.loc(), "Illegal field access."));
        return Optional.empty();
      }
    });
  }

  private Optional<TypeS> translateOrGenerateTempVar(Optional<TypeP> typeP) {
    return typeP.map(typePsTranslator::translate)
        .orElseGet(() -> Optional.of(unifier.newTempVar()));
  }
}
