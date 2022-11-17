package org.smoothbuild.compile.ps.infer;

import static com.google.common.collect.Maps.toMap;
import static org.smoothbuild.compile.lang.type.TypeFS.BLOB;
import static org.smoothbuild.compile.lang.type.TypeFS.INT;
import static org.smoothbuild.compile.lang.type.TypeFS.STRING;
import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.compile.ps.infer.InferPositionedArgs.inferPositionedArgs;
import static org.smoothbuild.util.collect.Lists.map;
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
import org.smoothbuild.compile.ps.ast.expr.DefaultArgP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.IntP;
import org.smoothbuild.compile.ps.ast.expr.MonoizableP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.OrderP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.SelectP;
import org.smoothbuild.compile.ps.ast.expr.StringP;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ExprTypeUnifier {
  private final Unifier unifier;
  private final Bindings<? extends Optional<? extends RefableS>> bindings;
  private final Logger logger;

  public ExprTypeUnifier(Unifier unifier, Bindings<? extends Optional<? extends RefableS>> bindings,
      Logger logger) {
    this.unifier = unifier;
    this.bindings = bindings;
    this.logger = logger;
  }

  public Optional<TypeS> unifyExpr(ExprP expr) {
    // @formatter:off
    return switch (expr) {
      case CallP       callP       -> unifyAndMemoize(this::unifyCall, callP);
      case DefaultArgP defaultArgP -> unifyAndMemoize(this::unifyDefaultArg, defaultArgP);
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
      Function<T, Optional<TypeS>> inferrer, T operP) {
    var type = inferrer.apply(operP);
    type.ifPresent(operP::setTypeS);
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
    var funcT = new FuncTS(resT, argTs);
    try {
      unifier.unify(funcT, calleeT);
      return Optional.of(resT);
    } catch (UnifierExc e) {
      logger.log(compileError(loc, "Illegal call."));
      return Optional.empty();
    }
  }

  private Optional<TypeS> unifyDefaultArg(DefaultArgP defaultArgP) {
    return unifyMonoizable(defaultArgP, defaultArgP.evaluableS().schema());
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
}
