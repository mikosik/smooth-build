package org.smoothbuild.compile.ps.infer;

import static com.google.common.collect.Maps.toMap;
import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.compile.ps.infer.InferPositionedArgs.inferPositionedArgs;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Optionals.flatMapPair;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.Optional;
import java.util.function.Function;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.define.FuncS;
import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.lang.define.PolyFuncS;
import org.smoothbuild.compile.lang.define.RefableS;
import org.smoothbuild.compile.lang.type.ArrayTS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.tool.Unifier;
import org.smoothbuild.compile.lang.type.tool.UnifierExc;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.DefaultArgP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.OperP;
import org.smoothbuild.compile.ps.ast.expr.OrderP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.SelectP;
import org.smoothbuild.compile.ps.ast.expr.ValP;
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
    return switch (expr) {
      case CallP callP -> unifyAndMemoize(this::unifyCall, callP);
      case ValP valP -> Optional.of(valP.type());
      case DefaultArgP defaultArgP -> unifyAndMemoize(this::unifyDefaultArg, defaultArgP);
      case NamedArgP namedArgP -> unifyAndMemoize(this::unifyNamedArg, namedArgP);
      case OrderP orderP -> unifyAndMemoize(this::unifyOrder, orderP);
      case RefP refP -> unifyRef(refP);
      case SelectP selectP -> unifyAndMemoize(this::unifySelect, selectP);
    };
  }

  private <T extends OperP> Optional<TypeS> unifyAndMemoize(
      Function<T, Optional<TypeS>> inferer, T expr) {
    var type = inferer.apply(expr);
    type.ifPresent(expr::setTypeS);
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
    return switch (callP.callee()) {
      case RefP refP -> bindings.get(refP.name())
          .flatMap(refableS -> inferPositionedArgs(callP, refP, refableParams(refableS), logger));
      default -> inferPositionedArgs(callP, null, Optional.empty(), logger);
    };
  }

  private static Optional<NList<ItemS>> refableParams(RefableS refableS) {
    return switch (refableS) {
      case FuncS funcS -> Optional.of(funcS.params());
      case PolyFuncS polyFuncS -> Optional.of(polyFuncS.mono().params());
      default -> Optional.empty();
    };
  }

  private Optional<TypeS> unifyCall(TypeS calleeT, ImmutableList<TypeS> argTs, Loc loc) {
    var resT = unifier.generateUniqueVar();
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
    var type = defaultArgP.exprS().type()
        .mapVars(defaultArgP.refP().monoizationMapper());
    return Optional.of(type);
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
    var elemVar = unifier.generateUniqueVar();
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
    return refable.flatMap((RefableS r) -> switch (r.typelike()) {
      case TypeS type -> unifyMonoRef(ref, type);
      case SchemaS schema -> unifyPolyRef(ref, schema);
    });
  }

  private static Optional<TypeS> unifyMonoRef(RefP ref, TypeS type) {
    ref.setMonoizationMapping(ImmutableMap.of());
    return Optional.of(type);
  }

  private Optional<TypeS> unifyPolyRef(RefP ref, SchemaS schema) {
    var monoizationMapping = toMap(
        schema.quantifiedVars().asList(), v -> unifier.generateUniqueVar());
    var mappedSchema = schema.monoize(monoizationMapping::get);
    ref.setMonoizationMapping(monoizationMapping);
    return Optional.of(mappedSchema);
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
