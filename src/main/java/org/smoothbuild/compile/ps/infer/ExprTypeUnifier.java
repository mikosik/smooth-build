package org.smoothbuild.compile.ps.infer;

import static com.google.common.collect.Maps.toMap;
import static org.smoothbuild.compile.lang.type.TypeFS.BLOB;
import static org.smoothbuild.compile.lang.type.TypeFS.INT;
import static org.smoothbuild.compile.lang.type.TypeFS.STRING;
import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.compile.ps.infer.BindingsHelper.funcBodyScopeBindings;
import static org.smoothbuild.compile.ps.infer.InferPositionedArgs.inferPositionedArgs;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.zip;
import static org.smoothbuild.util.collect.Optionals.flatMapPair;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.Optional;
import java.util.function.Function;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.lang.define.NamedFuncS;
import org.smoothbuild.compile.lang.define.RefableS;
import org.smoothbuild.compile.lang.type.ArrayTS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.tool.Unifier;
import org.smoothbuild.compile.lang.type.tool.UnifierExc;
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
  private final Logger logger;

  public ExprTypeUnifier(Unifier unifier, TypePsTranslator typePsTranslator,
      Bindings<? extends Optional<? extends RefableS>> bindings, Logger logger) {
    this.unifier = unifier;
    this.typePsTranslator = typePsTranslator;
    this.bindings = bindings;
    this.logger = logger;
  }

  public boolean unifyNamedFunc(NamedFuncP namedFunc) {
    var funcTS = inferParamTs(namedFunc.params())
        .flatMap(paramTs -> unifyNamedFunc(namedFunc, paramTs));
    return memoizeAndReturnTrueWhenTypeIsPresent(namedFunc, funcTS);
  }

  private Optional<TypeS> unifyNamedFunc(NamedFuncP namedFunc, ImmutableList<TypeS> paramTs) {
    var bodyBindings = funcBodyScopeBindings(bindings, namedFunc.params());
    return new ExprTypeUnifier(unifier, typePsTranslator, bodyBindings, logger)
        .unifyNamedFuncImpl(namedFunc, paramTs);
  }

  private Optional<TypeS> unifyNamedFuncImpl(NamedFuncP namedFunc, ImmutableList<TypeS> paramTs) {
    return translateOrGenerateTempVar(namedFunc.resT())
        .flatMap(resT -> handleBodyIfPresent(namedFunc, resT))
        .map(resT -> new FuncTS(paramTs, resT));
  }

  private Optional<ImmutableList<TypeS>> inferParamTs(NList<ItemP> params) {
    var paramTs = pullUp(map(params, p -> typePsTranslator.translate(p.type())));
    paramTs.ifPresent(types -> zip(params, types, ItemP::setTypeS));
    return paramTs;
  }

  public boolean unifyNamedValue(NamedValueP namedValue) {
    var typeS = translateOrGenerateTempVar(namedValue.type())
        .flatMap(t -> handleBodyIfPresent(namedValue, t));
    return memoizeAndReturnTrueWhenTypeIsPresent(namedValue, typeS);
  }

  private Optional<TypeS> handleBodyIfPresent(EvaluableP evaluable, TypeS typeS) {
    return evaluable.body()
        .map(body -> unifyBodyExprAndEvaluationType(evaluable, typeS, body))
        .orElseGet(() -> Optional.of(typeS));
  }

  private Optional<TypeS> unifyBodyExprAndEvaluationType(
      EvaluableP evaluable, TypeS typeS, ExprP body) {
    return unifyExpr(body)
        .flatMap(bodyT -> unifyEvaluationTypeWithBodyType(evaluable, typeS, bodyT));
  }

  private Optional<TypeS> unifyEvaluationTypeWithBodyType(
      EvaluableP evaluable, TypeS typeS, TypeS bodyT) {
    try {
      unifier.unify(typeS, bodyT);
      return Optional.of(typeS);
    } catch (UnifierExc e) {
      logger.log(compileError(
          evaluable.loc(), evaluable.q() + " body type is not equal to declared type."));
      return Optional.empty();
    }
  }

  private static boolean memoizeAndReturnTrueWhenTypeIsPresent(
      EvaluableP evaluable, Optional<? extends TypeS> typeS) {
    if (typeS.isPresent()) {
      evaluable.setTypeS(typeS.get());
      return true;
    } else {
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

  private Optional<TypeS> unifyNamedArg(NamedArgP namedArgP) {
    return unifyExpr(namedArgP.expr());
  }

  private Optional<TypeS> unifyOrder(OrderP order) {
    var elems = order.elems();
    var elemVars = pullUp(map(elems, this::unifyExpr));
    return elemVars.flatMap(elemTs -> unifyElemsWithArray(elemTs, order.loc()));
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
      case NamedEvaluableS evaluable -> unifyMonoizable(ref, evaluable.schema());
    };
  }

  private static Optional<TypeS> unifyItemRef(RefP ref, ItemS item) {
    ref.setMonoizeVarMap(ImmutableMap.of());
    return Optional.of(item.type());
  }

  private Optional<TypeS> unifyMonoizable(MonoizableP monoizableP, SchemaS schema) {
    monoizableP.setMonoizeVarMap(
        toMap(schema.quantifiedVars().asList(), v -> unifier.newTempVar()));
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
