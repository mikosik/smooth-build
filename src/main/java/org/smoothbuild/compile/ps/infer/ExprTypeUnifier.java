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

import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.lang.define.NamedFuncS;
import org.smoothbuild.compile.lang.define.RefableS;
import org.smoothbuild.compile.lang.type.ArrayTS;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarSetS;
import org.smoothbuild.compile.lang.type.tool.Unifier;
import org.smoothbuild.compile.lang.type.tool.UnifierExc;
import org.smoothbuild.compile.ps.ast.expr.AnonymousFuncP;
import org.smoothbuild.compile.ps.ast.expr.BlobP;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.EvaluableP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.FuncP;
import org.smoothbuild.compile.ps.ast.expr.IntP;
import org.smoothbuild.compile.ps.ast.expr.ItemP;
import org.smoothbuild.compile.ps.ast.expr.MonoizableP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.NamedFuncP;
import org.smoothbuild.compile.ps.ast.expr.NamedValueP;
import org.smoothbuild.compile.ps.ast.expr.OrderP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.SelectP;
import org.smoothbuild.compile.ps.ast.expr.StringP;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.util.bindings.OptionalBindings;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ExprTypeUnifier {
  private final Unifier unifier;
  private final TypePsTranslator typePsTranslator;
  private final OptionalBindings<? extends RefableS> bindings;
  private final VarSetS outerScopeVars;
  private final Logger logger;

  public ExprTypeUnifier(
      Unifier unifier,
      TypePsTranslator typePsTranslator,
      OptionalBindings<? extends RefableS> bindings,
      Logger logger) {
    this(unifier, typePsTranslator, bindings, varSetS(), logger);
  }

  public ExprTypeUnifier(
      Unifier unifier,
      TypePsTranslator typePsTranslator,
      OptionalBindings<? extends RefableS> bindings,
      VarSetS outerScopeVars,
      Logger logger) {
    this.unifier = unifier;
    this.typePsTranslator = typePsTranslator;
    this.bindings = bindings;
    this.outerScopeVars = outerScopeVars;
    this.logger = logger;
  }

  public boolean unifyNamedValue(NamedValueP namedValue) {
    return unifyEvaluableAndSetSchema(namedValue);
  }

  public boolean unifyNamedFunc(NamedFuncP namedFunc) {
    return unifyEvaluableAndSetSchema(namedFunc);
  }

  private boolean unifyEvaluableAndSetSchema(EvaluableP evaluableP) {
    return unifyEvaluable(evaluableP) && setEvaluableSchema(evaluableP);
  }

  private boolean unifyEvaluable(EvaluableP evaluableP) {
    return switch (evaluableP) {
      case FuncP funcP -> unifyFunc(funcP);
      case NamedValueP namedValueP -> unifyValue(namedValueP);
      // TODO remove once bug in intellij is fixed
      default -> throw new IllegalStateException("Unexpected value: " + evaluableP);
    };
  }

  private boolean setEvaluableSchema(EvaluableP evaluableP) {
    var resolvedT = resolveType(evaluableP);
    var vars = resolveQuantifiedVars(resolvedT);
    // @formatter:off
    switch (evaluableP) {
      case NamedValueP valueP -> valueP.setSchemaS(new SchemaS(vars, resolvedT));
      case FuncP       funcP  -> funcP.setSchemaS(new FuncSchemaS(vars, (FuncTS) resolvedT));
      // TODO remove once bug in intellij is fixed
      default -> throw new IllegalStateException("Unexpected value: " + evaluableP);
    }
    // @formatter:on
    return true;
  }

  private VarSetS resolveQuantifiedVars(TypeS typeS) {
    return typeS.vars().withRemoved(outerScopeVars.map(unifier::resolve));
  }

  private TypeS resolveType(EvaluableP evaluableP) {
    return unifier.resolve(evaluableP.typeS());
  }

  private boolean unifyValue(NamedValueP namedValueP) {
    return translateOrGenerateTempVar(namedValueP.evalT())
        .map(evalT -> {
          namedValueP.setTypeS(evalT);
          return unifyEvaluableBody(namedValueP, evalT, evalT, bindings);
        })
        .orElse(false);
  }

  private boolean unifyFunc(FuncP funcP) {
    var paramTs = inferParamTs(funcP.params());
    var resT = translateOrGenerateTempVar(funcP.resT());
    return mapPair(paramTs, resT, (p, r) -> unifyFunc(funcP, p, r))
        .orElse(false);
  }

  private boolean unifyFunc(FuncP funcP, ImmutableList<TypeS> paramTs, TypeS resT) {
    var paramsS = funcP.params().map(ExprTypeUnifier::itemS);
    var bodyBindings = funcBodyScopeBindings(bindings, paramsS);
    var funcTS = new FuncTS(paramTs, resT);
    funcP.setTypeS(funcTS);
    return unifyEvaluableBody(funcP, resT, funcTS, bodyBindings);
  }

  private static ItemS itemS(ItemP itemP) {
    return new ItemS(itemP.typeS(), itemP.name(), Optional.empty(), itemP.location());
  }

  private Optional<ImmutableList<TypeS>> inferParamTs(NList<ItemP> params) {
    var paramTs = pullUp(map(params, p -> typePsTranslator.translate(p.type())));
    paramTs.ifPresent(types -> zip(params, types, ItemP::setTypeS));
    return paramTs;
  }

  private Boolean unifyEvaluableBody(EvaluableP evaluableP, TypeS evalT, TypeS type,
      OptionalBindings<? extends RefableS> bindings) {
    var vars = outerScopeVars.withAdded(type.vars());
    return new ExprTypeUnifier(unifier, typePsTranslator, bindings, vars, logger)
        .unifyEvaluableBody(evaluableP, evalT);
  }

  private Boolean unifyEvaluableBody(EvaluableP evaluableP, TypeS evalT) {
    return evaluableP.body()
        .map(body -> unifyBodyExprAndEvaluationType(evaluableP, evalT, body))
        .orElse(true);
  }

  private boolean unifyBodyExprAndEvaluationType(EvaluableP evaluableP, TypeS typeS, ExprP bodyP) {
    return unifyExpr(bodyP)
        .map(bodyT -> unifyEvaluationTypeWithBodyType(evaluableP, typeS, bodyT))
        .orElse(false);
  }

  private boolean unifyEvaluationTypeWithBodyType(EvaluableP evaluableP, TypeS typeS, TypeS bodyT) {
    try {
      unifier.unify(typeS, bodyT);
      return true;
    } catch (UnifierExc e) {
      logger.log(compileError(
          evaluableP.location(), evaluableP.q() + " body type is not equal to declared type."));
      return false;
    }
  }

  private Optional<TypeS> unifyExpr(ExprP exprP) {
    // @formatter:off
    return switch (exprP) {
      case CallP          callP          -> unifyAndMemoize(this::unifyCall, callP);
      case AnonymousFuncP anonymousFuncP -> unifyAndMemoize(this::unifyAnonymousFunc, anonymousFuncP);
      case NamedArgP      namedArgP      -> unifyAndMemoize(this::unifyNamedArg, namedArgP);
      case OrderP         orderP         -> unifyAndMemoize(this::unifyOrder, orderP);
      case RefP           refP           -> unifyAndMemoize(this::unifyRef, refP);
      case SelectP        selectP        -> unifyAndMemoize(this::unifySelect, selectP);
      case StringP        stringP        -> setAndMemoize(STRING, stringP);
      case IntP           intP           -> setAndMemoize(INT, intP);
      case BlobP          blobP          -> setAndMemoize(BLOB, blobP);
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

  private Optional<TypeS> unifyCall(CallP callP) {
    var calleeT = unifyExpr(callP.callee());
    var positionedArgs = positionedArgs(callP);
    callP.setPositionedArgs(positionedArgs);
    if (positionedArgs.isPresent()) {
      var argTs = pullUp(map(positionedArgs.get(), this::unifyExpr));
      return flatMapPair(calleeT, argTs, (c, a) -> unifyCall(c, a, callP.location()));
    } else {
      map(callP.args(), this::unifyExpr);
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

  private Optional<TypeS> unifyCall(TypeS calleeT, ImmutableList<TypeS> argTs, Location location) {
    var resT = unifier.newTempVar();
    var funcT = new FuncTS(argTs, resT);
    try {
      unifier.unify(funcT, calleeT);
      return Optional.of(resT);
    } catch (UnifierExc e) {
      logger.log(compileError(location, "Illegal call."));
      return Optional.empty();
    }
  }

  private Optional<TypeS> unifyAnonymousFunc(AnonymousFuncP anonymousFuncP) {
    if (unifyEvaluableAndSetSchema(anonymousFuncP)) {
      return unifyMonoizable(anonymousFuncP);
    } else {
      return Optional.empty();
    }
  }

  private Optional<TypeS> unifyNamedArg(NamedArgP namedArgP) {
    return unifyExpr(namedArgP.expr());
  }

  private Optional<TypeS> unifyOrder(OrderP orderP) {
    var elems = orderP.elems();
    var elemTs = pullUp(map(elems, this::unifyExpr));
    return elemTs.flatMap(types -> unifyElemsWithArray(types, orderP.location()));
  }

  private Optional<TypeS> unifyElemsWithArray(ImmutableList<TypeS> elemTs, Location location) {
    var elemVar = unifier.newTempVar();
    for (TypeS elemT : elemTs) {
      try {
        unifier.unify(elemVar, elemT);
      } catch (UnifierExc e) {
        logger.log(compileError(location,
            "Cannot infer type for array literal. Its element types are not compatible."));
        return Optional.empty();
      }
    }
    return Optional.of(new ArrayTS(elemVar));
  }

  private Optional<TypeS> unifyRef(RefP refP) {
    var refable = bindings.get(refP.name());
    return refable.flatMap(r -> unifyRef(refP, r));
  }

  private Optional<? extends TypeS> unifyRef(RefP ref, RefableS refable) {
    return switch (refable) {
      case ItemS item -> unifyItemRef(ref, item);
      case NamedEvaluableS evaluable -> unifyEvaluableRef(ref, evaluable);
    };
  }

  private static Optional<TypeS> unifyItemRef(RefP refP, ItemS item) {
    refP.setMonoizeVarMap(ImmutableMap.of());
    return Optional.of(item.type());
  }

  private Optional<TypeS> unifyEvaluableRef(RefP refP, NamedEvaluableS namedEvaluableS) {
    refP.setSchemaS(namedEvaluableS.schema());
    return unifyMonoizable(refP);
  }

  private Optional<TypeS> unifyMonoizable(MonoizableP monoizableP) {
    var schema = monoizableP.schemaS();
    var varMap = toMap(schema.quantifiedVars(), v -> (TypeS) unifier.newTempVar());
    monoizableP.setMonoizeVarMap(varMap);
    return Optional.of(schema.monoize(monoizableP.monoizeVarMap()));
  }

  private Optional<TypeS> unifySelect(SelectP selectP) {
    var selectableT = unifyExpr(selectP.selectable());
    return selectableT.flatMap(t -> {
      if (unifier.resolve(t) instanceof StructTS structTS) {
        var itemSigS = structTS.fields().get(selectP.field());
        if (itemSigS == null) {
          logger.log(compileError(selectP.location(), "Unknown field `" + selectP.field() + "`."));
          return Optional.empty();
        } else {
          return Optional.of(itemSigS.type());
        }
      } else {
        logger.log(compileError(selectP.location(), "Illegal field access."));
        return Optional.empty();
      }
    });
  }

  private Optional<TypeS> translateOrGenerateTempVar(Optional<TypeP> typeP) {
    return typeP.map(typePsTranslator::translate)
        .orElseGet(() -> Optional.of(unifier.newTempVar()));
  }
}
