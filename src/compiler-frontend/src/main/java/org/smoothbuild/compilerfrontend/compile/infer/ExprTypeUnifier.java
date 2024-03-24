package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.collect.List.generateList;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.List.pullUpMaybe;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import java.util.function.Function;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.compilerfrontend.compile.ast.define.BlobP;
import org.smoothbuild.compilerfrontend.compile.ast.define.CallP;
import org.smoothbuild.compilerfrontend.compile.ast.define.EvaluableP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ExprP;
import org.smoothbuild.compilerfrontend.compile.ast.define.FuncP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ImplicitTP;
import org.smoothbuild.compilerfrontend.compile.ast.define.InstantiateP;
import org.smoothbuild.compilerfrontend.compile.ast.define.IntP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ItemP;
import org.smoothbuild.compilerfrontend.compile.ast.define.LambdaP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedArgP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedFuncP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedValueP;
import org.smoothbuild.compilerfrontend.compile.ast.define.OrderP;
import org.smoothbuild.compilerfrontend.compile.ast.define.PolymorphicP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ReferenceP;
import org.smoothbuild.compilerfrontend.compile.ast.define.SelectP;
import org.smoothbuild.compilerfrontend.compile.ast.define.StringP;
import org.smoothbuild.compilerfrontend.compile.ast.define.TypeP;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypes;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;
import org.smoothbuild.compilerfrontend.lang.type.tool.EqualityConstraint;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;
import org.smoothbuild.compilerfrontend.lang.type.tool.UnifierException;

public class ExprTypeUnifier {
  private final Unifier unifier;
  private final TypeTeller typeTeller;
  private final SVarSet outerScopeVars;
  private final Logger logger;

  public ExprTypeUnifier(Unifier unifier, TypeTeller typeTeller, Logger logger) {
    this(unifier, typeTeller, varSetS(), logger);
  }

  private ExprTypeUnifier(
      Unifier unifier, TypeTeller typeTeller, SVarSet outerScopeVars, Logger logger) {
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
    switch (evaluableP) {
      case NamedValueP valueP -> valueP.setSchemaS(new SchemaS(vars, resolvedT));
      case FuncP funcP -> funcP.setSchemaS(new SFuncSchema(vars, (SFuncType) resolvedT));
    }
    return true;
  }

  private SVarSet resolveQuantifiedVars(SType sType) {
    return sType.vars().withRemovedAll(outerScopeVars.map(unifier::resolve));
  }

  private SType resolveType(EvaluableP evaluableP) {
    return unifier.resolve(evaluableP.typeS());
  }

  private boolean unifyValue(NamedValueP namedValueP) {
    return translateOrGenerateTempVar(namedValueP.evaluationType())
        .map(evaluationType -> {
          namedValueP.setTypeS(evaluationType);
          return unifyEvaluableBody(namedValueP, evaluationType, evaluationType, typeTeller);
        })
        .getOr(false);
  }

  private boolean unifyFunc(FuncP funcP) {
    var paramTs = inferParamTs(funcP.params());
    var resultT = translateOrGenerateTempVar(funcP.resultT());
    return paramTs.mapWith(resultT, (p, r) -> unifyFunc(funcP, p, r)).getOr(false);
  }

  private boolean unifyFunc(FuncP funcP, List<SType> paramTs, SType resultT) {
    var typeTellerForBody = typeTeller.withScope(funcP.scope());
    var funcTS = new SFuncType(paramTs, resultT);
    funcP.setTypeS(funcTS);
    return unifyEvaluableBody(funcP, resultT, funcTS, typeTellerForBody);
  }

  private Maybe<List<SType>> inferParamTs(NList<ItemP> params) {
    var paramTs = pullUpMaybe(params.list().map(p -> typeTeller.translate(p.type())));
    paramTs.ifPresent(types -> params.list().zip(types, ItemP::setTypeS));
    return paramTs.map(List::listOfAll);
  }

  private Boolean unifyEvaluableBody(
      EvaluableP evaluableP, SType evaluationType, SType type, TypeTeller typeTeller) {
    var vars = outerScopeVars.withAddedAll(type.vars());
    return new ExprTypeUnifier(unifier, typeTeller, vars, logger)
        .unifyEvaluableBody(evaluableP, evaluationType);
  }

  private Boolean unifyEvaluableBody(EvaluableP evaluableP, SType evaluationType) {
    return evaluableP
        .body()
        .map(body -> unifyBodyExprAndEvaluationType(evaluableP, evaluationType, body))
        .getOr(true);
  }

  private boolean unifyBodyExprAndEvaluationType(EvaluableP evaluableP, SType sType, ExprP bodyP) {
    return unifyExpr(bodyP)
        .map(bodyT -> unifyEvaluationTypeWithBodyType(evaluableP, sType, bodyT))
        .getOr(false);
  }

  private boolean unifyEvaluationTypeWithBodyType(EvaluableP evaluableP, SType sType, SType bodyT) {
    try {
      unify(sType, bodyT);
      return true;
    } catch (UnifierException e) {
      logger.log(compileError(
          evaluableP.location(), evaluableP.q() + " body type is not equal to declared type."));
      return false;
    }
  }

  private Maybe<SType> unifyExpr(ExprP exprP) {
    return switch (exprP) {
      case CallP callP -> unifyAndMemoize(this::unifyCall, callP);
      case InstantiateP instantiateP -> unifyAndMemoize(this::unifyInstantiate, instantiateP);
      case NamedArgP namedArgP -> unifyAndMemoize(this::unifyNamedArg, namedArgP);
      case OrderP orderP -> unifyAndMemoize(this::unifyOrder, orderP);
      case SelectP selectP -> unifyAndMemoize(this::unifySelect, selectP);
      case StringP stringP -> setAndMemoize(STypes.STRING, stringP);
      case IntP intP -> setAndMemoize(STypes.INT, intP);
      case BlobP blobP -> setAndMemoize(STypes.BLOB, blobP);
    };
  }

  private Maybe<SType> setAndMemoize(SType sType, ExprP exprP) {
    exprP.setTypeS(sType);
    return some(sType);
  }

  private <T extends ExprP> Maybe<SType> unifyAndMemoize(
      Function<T, Maybe<SType>> unifier, T exprP) {
    return unifier.apply(exprP).ifPresent(exprP::setTypeS);
  }

  private Maybe<SType> unifyCall(CallP callP) {
    var calleeT = unifyExpr(callP.callee());
    var positionedArgs = callP.positionedArgs();
    var argTs = pullUpMaybe(positionedArgs.map(this::unifyExpr));
    return calleeT.flatMapWith(argTs, (c, a) -> unifyCall(c, listOfAll(a), callP.location()));
  }

  private Maybe<SType> unifyCall(SType calleeT, List<SType> argTs, Location location) {
    var resultT = unifier.newTempVar();
    var funcT = new SFuncType(argTs, resultT);
    try {
      unify(funcT, calleeT);
      return some(resultT);
    } catch (UnifierException e) {
      logger.log(compileError(location, "Illegal call."));
      return none();
    }
  }

  private Maybe<SType> unifyInstantiate(InstantiateP instantiateP) {
    var polymorphicP = instantiateP.polymorphic();
    if (unifyPolymorphic(polymorphicP)) {
      var schema = polymorphicP.schemaS();
      instantiateP.setTypeArgs(generateList(schema.quantifiedVars().size(), unifier::newTempVar));
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

  private Maybe<SType> unifyNamedArg(NamedArgP namedArgP) {
    return unifyExpr(namedArgP.expr());
  }

  private Maybe<SType> unifyOrder(OrderP orderP) {
    var elems = orderP.elements();
    var elemTs = pullUpMaybe(elems.map(this::unifyExpr));
    return elemTs.flatMap(types -> unifyElementsWithArray(listOfAll(types), orderP.location()));
  }

  private Maybe<SType> unifyElementsWithArray(List<SType> elemTs, Location location) {
    var elemVar = unifier.newTempVar();
    for (SType elemT : elemTs) {
      try {
        unify(elemVar, elemT);
      } catch (UnifierException e) {
        logger.log(compileError(
            location,
            "Cannot infer type for array literal. Its element types are not compatible."));
        return none();
      }
    }
    return some(new SArrayType(elemVar));
  }

  private boolean unifyReference(ReferenceP referenceP) {
    Maybe<SchemaS> schemaS = typeTeller.schemaFor(referenceP.referencedName());
    if (schemaS.isSome()) {
      referenceP.setSchemaS(schemaS.get());
      return true;
    } else {
      return false;
    }
  }

  private Maybe<SType> unifySelect(SelectP selectP) {
    var selectableT = unifyExpr(selectP.selectable());
    return selectableT.flatMap(t -> {
      if (unifier.resolve(t) instanceof SStructType sStructType) {
        var itemSigS = sStructType.fields().get(selectP.field());
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

  private void unify(SType sType, SType bodyT) throws UnifierException {
    unifier.add(new EqualityConstraint(sType, bodyT));
  }

  private Maybe<SType> translateOrGenerateTempVar(TypeP typeP) {
    if (typeP instanceof ImplicitTP) {
      return some(unifier.newTempVar());
    } else {
      return typeTeller.translate(typeP);
    }
  }
}
