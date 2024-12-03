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
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.compile.ast.define.PBlob;
import org.smoothbuild.compilerfrontend.compile.ast.define.PCall;
import org.smoothbuild.compilerfrontend.compile.ast.define.PEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PImplicitType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInstantiate;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInt;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedArg;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.POrder;
import org.smoothbuild.compilerfrontend.compile.ast.define.PPolymorphic;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PSelect;
import org.smoothbuild.compilerfrontend.compile.ast.define.PString;
import org.smoothbuild.compilerfrontend.compile.ast.define.PType;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypes;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;
import org.smoothbuild.compilerfrontend.lang.type.tool.EqualityConstraint;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;
import org.smoothbuild.compilerfrontend.lang.type.tool.UnifierException;

public class ExprTypeUnifier {
  private final Unifier unifier;
  private final TypeTeller typeTeller;
  private final SVarSet outerScopeVars;
  private final Logger logger;

  private ExprTypeUnifier(Unifier unifier, TypeTeller typeTeller, Logger logger) {
    this(unifier, typeTeller, varSetS(), logger);
  }

  private ExprTypeUnifier(
      Unifier unifier, TypeTeller typeTeller, SVarSet outerScopeVars, Logger logger) {
    this.unifier = unifier;
    this.typeTeller = typeTeller;
    this.outerScopeVars = outerScopeVars;
    this.logger = logger;
  }

  public static boolean unifyNamedValue(
      Unifier unifier, TypeTeller typeTeller, Logger logger, PNamedValue pNamedValue) {
    return new ExprTypeUnifier(unifier, typeTeller, logger).unifyNamedValue(pNamedValue);
  }

  private boolean unifyNamedValue(PNamedValue pNamedValue) {
    return unifyValue(pNamedValue) && setNamedValueSchema(pNamedValue);
  }

  private boolean setNamedValueSchema(PNamedValue pNamedValue) {
    var resolvedT = resolveType(pNamedValue);
    var vars = resolveQuantifiedVars(resolvedT);
    pNamedValue.setSSchema(new SSchema(vars, resolvedT));
    return true;
  }

  public static boolean unifyFunc(
      Unifier unifier, TypeTeller typeTeller, Logger logger, PFunc namedFunc) {
    return new ExprTypeUnifier(unifier, typeTeller, logger).unifyFunc(namedFunc);
  }

  private boolean unifyFunc(PFunc namedFunc) {
    return unifyFuncImpl(namedFunc) && setFuncSchema(namedFunc);
  }

  private boolean setFuncSchema(PFunc pFunc) {
    var resolvedT = resolveType(pFunc);
    var vars = resolveQuantifiedVars(resolvedT);
    pFunc.setSSchema(new SFuncSchema(vars, (SFuncType) resolvedT));
    return true;
  }

  private SVarSet resolveQuantifiedVars(SType sType) {
    return sType.vars().withRemovedAll(outerScopeVars.map(unifier::resolve));
  }

  private SType resolveType(PEvaluable pEvaluable) {
    return unifier.resolve(pEvaluable.sType());
  }

  private boolean unifyValue(PNamedValue pNamedValue) {
    return translateOrGenerateTempVar(pNamedValue.evaluationType())
        .map(evaluationType -> {
          pNamedValue.setSType(evaluationType);
          return unifyEvaluableBody(pNamedValue, evaluationType, evaluationType, typeTeller);
        })
        .getOr(false);
  }

  private boolean unifyFuncImpl(PFunc pFunc) {
    var paramTs = inferParamTs(pFunc.params());
    var resultT = translateOrGenerateTempVar(pFunc.resultT());
    return paramTs.mapWith(resultT, (p, r) -> unifyFuncImpl(pFunc, p, r)).getOr(false);
  }

  private boolean unifyFuncImpl(PFunc pFunc, List<SType> paramTs, SType resultT) {
    var typeTellerForBody = typeTeller.withScope(pFunc.scope());
    var funcTS = new SFuncType(paramTs, resultT);
    pFunc.setSType(funcTS);
    return unifyEvaluableBody(pFunc, resultT, funcTS, typeTellerForBody);
  }

  private Maybe<List<SType>> inferParamTs(NList<PItem> params) {
    var paramTs = pullUpMaybe(params.list().map(p -> typeTeller.translate(p.type())));
    paramTs.ifPresent(types -> params.list().zip(types, PItem::setSType));
    return paramTs.map(List::listOfAll);
  }

  private boolean unifyEvaluableBody(
      PEvaluable pEvaluable, SType evaluationType, SType type, TypeTeller typeTeller) {
    var vars = outerScopeVars.withAddedAll(type.vars());
    return new ExprTypeUnifier(unifier, typeTeller, vars, logger)
        .unifyEvaluableBody(pEvaluable, evaluationType);
  }

  private boolean unifyEvaluableBody(PEvaluable pEvaluable, SType evaluationType) {
    return pEvaluable
        .body()
        .map(body -> unifyBodyExprAndEvaluationType(pEvaluable, evaluationType, body))
        .getOr(true);
  }

  private boolean unifyBodyExprAndEvaluationType(PEvaluable pEvaluable, SType sType, PExpr bodyP) {
    return unifyExpr(bodyP)
        .map(bodyT -> unifyEvaluationTypeWithBodyType(pEvaluable, sType, bodyT))
        .getOr(false);
  }

  private boolean unifyEvaluationTypeWithBodyType(PEvaluable pEvaluable, SType sType, SType bodyT) {
    try {
      unify(sType, bodyT);
      return true;
    } catch (UnifierException e) {
      logger.log(compileError(
          pEvaluable.location(), pEvaluable.q() + " body type is not equal to declared type."));
      return false;
    }
  }

  private Maybe<SType> unifyExpr(PExpr pExpr) {
    return switch (pExpr) {
      case PCall pCall -> unifyAndMemoize(this::unifyCall, pCall);
      case PInstantiate pInstantiate -> unifyAndMemoize(this::unifyInstantiate, pInstantiate);
      case PNamedArg pNamedArg -> unifyAndMemoize(this::unifyNamedArg, pNamedArg);
      case POrder pOrder -> unifyAndMemoize(this::unifyOrder, pOrder);
      case PSelect pSelect -> unifyAndMemoize(this::unifySelect, pSelect);
      case PString pString -> setAndMemoize(STypes.STRING, pString);
      case PInt pInt -> setAndMemoize(STypes.INT, pInt);
      case PBlob pBlob -> setAndMemoize(STypes.BLOB, pBlob);
    };
  }

  private Maybe<SType> setAndMemoize(SType sType, PExpr pExpr) {
    pExpr.setSType(sType);
    return some(sType);
  }

  private <T extends PExpr> Maybe<SType> unifyAndMemoize(
      Function<T, Maybe<SType>> unifier, T exprP) {
    return unifier.apply(exprP).ifPresent(exprP::setSType);
  }

  private Maybe<SType> unifyCall(PCall pCall) {
    var calleeT = unifyExpr(pCall.callee());
    var positionedArgs = pCall.positionedArgs();
    var argTs = pullUpMaybe(positionedArgs.map(this::unifyExpr));
    return calleeT.flatMapWith(argTs, (c, a) -> unifyCall(c, listOfAll(a), pCall.location()));
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

  private Maybe<SType> unifyInstantiate(PInstantiate pInstantiate) {
    var polymorphicP = pInstantiate.polymorphic();
    if (unifyPolymorphic(polymorphicP)) {
      var schema = polymorphicP.sSchema();
      pInstantiate.setTypeArgs(generateList(schema.quantifiedVars().size(), unifier::newTempVar));
      return some(schema.instantiate(pInstantiate.typeArgs()));
    } else {
      return none();
    }
  }

  private boolean unifyPolymorphic(PPolymorphic pPolymorphic) {
    return switch (pPolymorphic) {
      case PLambda pLambda -> unifyFunc(pLambda);
      case PReference pReference -> unifyReference(pReference);
    };
  }

  private Maybe<SType> unifyNamedArg(PNamedArg pNamedArg) {
    return unifyExpr(pNamedArg.expr());
  }

  private Maybe<SType> unifyOrder(POrder pOrder) {
    var elems = pOrder.elements();
    var elemTs = pullUpMaybe(elems.map(this::unifyExpr));
    return elemTs.flatMap(types -> unifyElementsWithArray(listOfAll(types), pOrder.location()));
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

  private boolean unifyReference(PReference pReference) {
    Maybe<SSchema> sSchema = typeTeller.schemaFor(pReference.referencedName());
    if (sSchema.isSome()) {
      pReference.setSSchema(sSchema.get());
      return true;
    } else {
      return false;
    }
  }

  private Maybe<SType> unifySelect(PSelect pSelect) {
    var selectableT = unifyExpr(pSelect.selectable());
    return selectableT.flatMap(t -> {
      if (unifier.resolve(t) instanceof SStructType sStructType) {
        var itemSigS = sStructType.fields().get(pSelect.field());
        if (itemSigS == null) {
          logger.log(compileError(pSelect.location(), "Unknown field `" + pSelect.field() + "`."));
          return none();
        } else {
          return some(itemSigS.type());
        }
      } else {
        logger.log(compileError(pSelect.location(), "Illegal field access."));
        return none();
      }
    });
  }

  private void unify(SType sType, SType bodyT) throws UnifierException {
    unifier.add(new EqualityConstraint(sType, bodyT));
  }

  private Maybe<SType> translateOrGenerateTempVar(PType pType) {
    if (pType instanceof PImplicitType) {
      return some(unifier.newTempVar());
    } else {
      return typeTeller.translate(pType);
    }
  }
}
