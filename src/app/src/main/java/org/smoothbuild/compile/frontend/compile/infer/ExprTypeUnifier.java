package org.smoothbuild.compile.frontend.compile.infer;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.List.pullUpMaybe;
import static org.smoothbuild.common.collect.Lists.zip;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.compile.frontend.compile.CompileError.compileError;
import static org.smoothbuild.compile.frontend.lang.type.VarSetS.varSetS;

import java.util.function.Function;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compile.frontend.compile.CompileError;
import org.smoothbuild.compile.frontend.compile.ast.define.BlobP;
import org.smoothbuild.compile.frontend.compile.ast.define.CallP;
import org.smoothbuild.compile.frontend.compile.ast.define.EvaluableP;
import org.smoothbuild.compile.frontend.compile.ast.define.ExprP;
import org.smoothbuild.compile.frontend.compile.ast.define.FuncP;
import org.smoothbuild.compile.frontend.compile.ast.define.ImplicitTP;
import org.smoothbuild.compile.frontend.compile.ast.define.InstantiateP;
import org.smoothbuild.compile.frontend.compile.ast.define.IntP;
import org.smoothbuild.compile.frontend.compile.ast.define.ItemP;
import org.smoothbuild.compile.frontend.compile.ast.define.LambdaP;
import org.smoothbuild.compile.frontend.compile.ast.define.NamedArgP;
import org.smoothbuild.compile.frontend.compile.ast.define.NamedFuncP;
import org.smoothbuild.compile.frontend.compile.ast.define.NamedValueP;
import org.smoothbuild.compile.frontend.compile.ast.define.OrderP;
import org.smoothbuild.compile.frontend.compile.ast.define.PolymorphicP;
import org.smoothbuild.compile.frontend.compile.ast.define.ReferenceP;
import org.smoothbuild.compile.frontend.compile.ast.define.SelectP;
import org.smoothbuild.compile.frontend.compile.ast.define.StringP;
import org.smoothbuild.compile.frontend.compile.ast.define.TypeP;
import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.compile.frontend.lang.type.ArrayTS;
import org.smoothbuild.compile.frontend.lang.type.FuncSchemaS;
import org.smoothbuild.compile.frontend.lang.type.FuncTS;
import org.smoothbuild.compile.frontend.lang.type.SchemaS;
import org.smoothbuild.compile.frontend.lang.type.StructTS;
import org.smoothbuild.compile.frontend.lang.type.TypeFS;
import org.smoothbuild.compile.frontend.lang.type.TypeS;
import org.smoothbuild.compile.frontend.lang.type.VarSetS;
import org.smoothbuild.compile.frontend.lang.type.tool.EqualityConstraint;
import org.smoothbuild.compile.frontend.lang.type.tool.Unifier;
import org.smoothbuild.compile.frontend.lang.type.tool.UnifierException;
import org.smoothbuild.out.log.Logger;

public class ExprTypeUnifier {
  private final Unifier unifier;
  private final TypeTeller typeTeller;
  private final VarSetS outerScopeVars;
  private final Logger logger;

  public ExprTypeUnifier(Unifier unifier, TypeTeller typeTeller, Logger logger) {
    this(unifier, typeTeller, varSetS(), logger);
  }

  private ExprTypeUnifier(
      Unifier unifier, TypeTeller typeTeller, VarSetS outerScopeVars, Logger logger) {
    this.unifier = unifier;
    this.typeTeller = typeTeller;
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
    };
  }

  private boolean setEvaluableSchema(EvaluableP evaluableP) {
    var resolvedT = resolveType(evaluableP);
    var vars = resolveQuantifiedVars(resolvedT);
    // @formatter:off
    switch (evaluableP) {
      case NamedValueP valueP -> valueP.setSchemaS(new SchemaS(vars, resolvedT));
      case FuncP funcP -> funcP.setSchemaS(new FuncSchemaS(vars, (FuncTS) resolvedT));
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
    return translateOrGenerateTempVar(namedValueP.evaluationT())
        .map(evaluationT -> {
          namedValueP.setTypeS(evaluationT);
          return unifyEvaluableBody(namedValueP, evaluationT, evaluationT, typeTeller);
        })
        .getOr(false);
  }

  private boolean unifyFunc(FuncP funcP) {
    var paramTs = inferParamTs(funcP.params());
    var resultT = translateOrGenerateTempVar(funcP.resultT());
    return paramTs.mapWith(resultT, (p, r) -> unifyFunc(funcP, p, r)).getOr(false);
  }

  private boolean unifyFunc(FuncP funcP, List<TypeS> paramTs, TypeS resultT) {
    var typeTellerForBody = typeTeller.withScope(funcP.scope());
    var funcTS = new FuncTS(paramTs, resultT);
    funcP.setTypeS(funcTS);
    return unifyEvaluableBody(funcP, resultT, funcTS, typeTellerForBody);
  }

  private Maybe<List<TypeS>> inferParamTs(NList<ItemP> params) {
    var paramTs = pullUpMaybe(params.list().map(p -> typeTeller.translate(p.type())));
    paramTs.toList().forEach(types -> zip(params, types, ItemP::setTypeS));
    return paramTs.map(List::listOfAll);
  }

  private Boolean unifyEvaluableBody(
      EvaluableP evaluableP, TypeS evaluationT, TypeS type, TypeTeller typeTeller) {
    var vars = outerScopeVars.withAdded(type.vars());
    return new ExprTypeUnifier(unifier, typeTeller, vars, logger)
        .unifyEvaluableBody(evaluableP, evaluationT);
  }

  private Boolean unifyEvaluableBody(EvaluableP evaluableP, TypeS evaluationT) {
    return evaluableP
        .body()
        .map(body -> unifyBodyExprAndEvaluationType(evaluableP, evaluationT, body))
        .getOr(true);
  }

  private boolean unifyBodyExprAndEvaluationType(EvaluableP evaluableP, TypeS typeS, ExprP bodyP) {
    return unifyExpr(bodyP)
        .map(bodyT -> unifyEvaluationTypeWithBodyType(evaluableP, typeS, bodyT))
        .getOr(false);
  }

  private boolean unifyEvaluationTypeWithBodyType(EvaluableP evaluableP, TypeS typeS, TypeS bodyT) {
    try {
      unify(typeS, bodyT);
      return true;
    } catch (UnifierException e) {
      logger.log(compileError(
          evaluableP.location(), evaluableP.q() + " body type is not equal to declared type."));
      return false;
    }
  }

  private Maybe<TypeS> unifyExpr(ExprP exprP) {
    // @formatter:off
    return switch (exprP) {
      case CallP callP -> unifyAndMemoize(this::unifyCall, callP);
      case InstantiateP instantiateP -> unifyAndMemoize(this::unifyInstantiate, instantiateP);
      case NamedArgP namedArgP -> unifyAndMemoize(this::unifyNamedArg, namedArgP);
      case OrderP orderP -> unifyAndMemoize(this::unifyOrder, orderP);
      case SelectP selectP -> unifyAndMemoize(this::unifySelect, selectP);
      case StringP stringP -> setAndMemoize(TypeFS.STRING, stringP);
      case IntP intP -> setAndMemoize(TypeFS.INT, intP);
      case BlobP blobP -> setAndMemoize(TypeFS.BLOB, blobP);
    };
    // @formatter:on
  }

  private Maybe<TypeS> setAndMemoize(TypeS typeS, ExprP exprP) {
    exprP.setTypeS(typeS);
    return some(typeS);
  }

  private <T extends ExprP> Maybe<TypeS> unifyAndMemoize(
      Function<T, Maybe<TypeS>> unifier, T exprP) {
    var type = unifier.apply(exprP);
    type.toList().forEach(exprP::setTypeS);
    return type;
  }

  private Maybe<TypeS> unifyCall(CallP callP) {
    var calleeT = unifyExpr(callP.callee());
    var positionedArgs = callP.positionedArgs();
    var argTs = pullUpMaybe(positionedArgs.map(this::unifyExpr));
    return calleeT.flatMapWith(argTs, (c, a) -> unifyCall(c, listOfAll(a), callP.location()));
  }

  private Maybe<TypeS> unifyCall(TypeS calleeT, List<TypeS> argTs, Location location) {
    var resultT = unifier.newTempVar();
    var funcT = new FuncTS(argTs, resultT);
    try {
      unify(funcT, calleeT);
      return some(resultT);
    } catch (UnifierException e) {
      logger.log(CompileError.compileError(location, "Illegal call."));
      return none();
    }
  }

  private Maybe<TypeS> unifyInstantiate(InstantiateP instantiateP) {
    var polymorphicP = instantiateP.polymorphic();
    if (unifyPolymorphic(polymorphicP)) {
      var schema = polymorphicP.schemaS();
      instantiateP.setTypeArgs(list(schema.quantifiedVars().size(), unifier::newTempVar));
      return some(schema.instantiate(instantiateP.typeArgs()));
    } else {
      return none();
    }
  }

  private boolean unifyPolymorphic(PolymorphicP polymorphicP) {
    return switch (polymorphicP) {
      case LambdaP lambdaP -> unifyLambdaFunc(lambdaP);
      case ReferenceP referenceP -> unifyReference(referenceP);
    };
  }

  private boolean unifyLambdaFunc(LambdaP lambdaP) {
    return unifyEvaluableAndSetSchema(lambdaP);
  }

  private Maybe<TypeS> unifyNamedArg(NamedArgP namedArgP) {
    return unifyExpr(namedArgP.expr());
  }

  private Maybe<TypeS> unifyOrder(OrderP orderP) {
    var elems = orderP.elems();
    var elemTs = pullUpMaybe(elems.map(this::unifyExpr));
    return elemTs.flatMap(types -> unifyElemsWithArray(listOfAll(types), orderP.location()));
  }

  private Maybe<TypeS> unifyElemsWithArray(List<TypeS> elemTs, Location location) {
    var elemVar = unifier.newTempVar();
    for (TypeS elemT : elemTs) {
      try {
        unify(elemVar, elemT);
      } catch (UnifierException e) {
        logger.log(CompileError.compileError(
            location,
            "Cannot infer type for array literal. Its element types are not compatible."));
        return none();
      }
    }
    return some(new ArrayTS(elemVar));
  }

  private boolean unifyReference(ReferenceP referenceP) {
    Maybe<SchemaS> schemaS = typeTeller.schemaFor(referenceP.name());
    if (schemaS.isSome()) {
      referenceP.setSchemaS(schemaS.get());
      return true;
    } else {
      return false;
    }
  }

  private Maybe<TypeS> unifySelect(SelectP selectP) {
    var selectableT = unifyExpr(selectP.selectable());
    return selectableT.flatMap(t -> {
      if (unifier.resolve(t) instanceof StructTS structTS) {
        var itemSigS = structTS.fields().get(selectP.field());
        if (itemSigS == null) {
          logger.log(compileError(selectP.location(), "Unknown field `" + selectP.field() + "`."));
          return none();
        } else {
          return some(itemSigS.type());
        }
      } else {
        logger.log(compileError(selectP.location(), "Illegal field access."));
        return none();
      }
    });
  }

  private void unify(TypeS typeS, TypeS bodyT) throws UnifierException {
    unifier.add(new EqualityConstraint(typeS, bodyT));
  }

  private Maybe<TypeS> translateOrGenerateTempVar(TypeP typeP) {
    if (typeP instanceof ImplicitTP) {
      return some(unifier.newTempVar());
    } else {
      return typeTeller.translate(typeP);
    }
  }
}
