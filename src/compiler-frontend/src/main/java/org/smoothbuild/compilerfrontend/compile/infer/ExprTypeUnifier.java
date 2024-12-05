package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.collect.List.generateList;
import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.common.function.Function1;
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
import org.smoothbuild.compilerfrontend.lang.type.tool.Constraint;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;
import org.smoothbuild.compilerfrontend.lang.type.tool.UnifierException;

public class ExprTypeUnifier {
  private final Unifier unifier;
  private final TypeTeller typeTeller;
  private final SVarSet outerScopeVars;

  private ExprTypeUnifier(Unifier unifier, TypeTeller typeTeller) throws TypeException {
    this(unifier, typeTeller, varSetS());
  }

  private ExprTypeUnifier(Unifier unifier, TypeTeller typeTeller, SVarSet outerScopeVars) {
    this.unifier = unifier;
    this.typeTeller = typeTeller;
    this.outerScopeVars = outerScopeVars;
  }

  public static void unifyNamedValue(
      Unifier unifier, TypeTeller typeTeller, PNamedValue pNamedValue) throws TypeException {
    new ExprTypeUnifier(unifier, typeTeller).unifyNamedValue(pNamedValue);
  }

  private void unifyNamedValue(PNamedValue pNamedValue) throws TypeException {
    var evaluationType = translateOrGenerateFlexibleVar(pNamedValue.evaluationType());
    pNamedValue.setSType(evaluationType);
    unifyEvaluableBody(pNamedValue, evaluationType, evaluationType, typeTeller);
    var resolvedType = resolveType(pNamedValue);
    var vars = resolveQuantifiedVars(resolvedType);
    pNamedValue.setSSchema(new SSchema(vars, resolvedType));
  }

  public static void unifyFunc(Unifier unifier, TypeTeller typeTeller, PFunc namedFunc)
      throws TypeException {
    new ExprTypeUnifier(unifier, typeTeller).unifyFunc(namedFunc);
  }

  private void unifyFunc(PFunc namedFunc) throws TypeException {
    var paramTypes = inferParamTypes(namedFunc.params());
    var resultType = translateOrGenerateFlexibleVar(namedFunc.resultT());
    var funcTS = new SFuncType(paramTypes, resultType);
    namedFunc.setSType(funcTS);
    unifyEvaluableBody(namedFunc, resultType, funcTS, typeTeller.withScope(namedFunc.scope()));
    var resolvedT = resolveType(namedFunc);
    var vars = resolveQuantifiedVars(resolvedT);
    namedFunc.setSSchema(new SFuncSchema(vars, (SFuncType) resolvedT));
  }

  private SVarSet resolveQuantifiedVars(SType sType) {
    return sType.vars().withRemovedAll(outerScopeVars.map(unifier::resolve));
  }

  private SType resolveType(PEvaluable pEvaluable) {
    return unifier.resolve(pEvaluable.sType());
  }

  private List<SType> inferParamTypes(NList<PItem> params) {
    var paramTypes = params.list().map(p -> typeTeller.translate(p.type()));
    params.list().zip(paramTypes, PItem::setSType);
    return paramTypes;
  }

  private void unifyEvaluableBody(
      PEvaluable pEvaluable, SType evaluationType, SType type, TypeTeller typeTeller)
      throws TypeException {
    var vars = outerScopeVars.withAddedAll(type.vars());
    new ExprTypeUnifier(unifier, typeTeller, vars).unifyEvaluableBody(pEvaluable, evaluationType);
  }

  private void unifyEvaluableBody(PEvaluable pEvaluable, SType evaluationType)
      throws TypeException {
    pEvaluable
        .body()
        .ifPresent(body -> unifyBodyExprAndEvaluationType(pEvaluable, evaluationType, body));
  }

  private void unifyBodyExprAndEvaluationType(
      PEvaluable pEvaluable, SType evaluationType, PExpr pBody) throws TypeException {
    var bodyType = unifyExpr(pBody);
    unifyEvaluationTypeWithBodyType(pEvaluable, evaluationType, bodyType);
  }

  private void unifyEvaluationTypeWithBodyType(
      PEvaluable pEvaluable, SType evaluationType, SType bodyType) throws TypeException {
    try {
      unify(evaluationType, bodyType);
    } catch (UnifierException e) {
      throw new TypeException(
          compileError(
              pEvaluable.location(), pEvaluable.q() + " body type is not equal to declared type."),
          e);
    }
  }

  private SType unifyExpr(PExpr pExpr) throws TypeException {
    return switch (pExpr) {
      case PCall pCall -> unifyAndMemoize(pCall, this::unifyCall);
      case PInstantiate pInstantiate -> unifyAndMemoize(pInstantiate, this::unifyInstantiate);
      case PNamedArg pNamedArg -> unifyAndMemoize(pNamedArg, this::unifyNamedArg);
      case POrder pOrder -> unifyAndMemoize(pOrder, this::unifyOrder);
      case PSelect pSelect -> unifyAndMemoize(pSelect, this::unifySelect);
      case PString pString -> setAndMemoize(pString, STypes.STRING);
      case PInt pInt -> setAndMemoize(pInt, STypes.INT);
      case PBlob pBlob -> setAndMemoize(pBlob, STypes.BLOB);
    };
  }

  private SType setAndMemoize(PExpr pExpr, SType sType) {
    pExpr.setSType(sType);
    return sType;
  }

  private <T extends PExpr> SType unifyAndMemoize(
      T exprP, Function1<T, SType, TypeException> unifier) throws TypeException {
    var type = unifier.apply(exprP);
    exprP.setSType(type);
    return type;
  }

  private SType unifyCall(PCall pCall) throws TypeException {
    var calleeType = unifyExpr(pCall.callee());
    var positionedArgs = pCall.positionedArgs();
    var argTypes = positionedArgs.map(this::unifyExpr);
    return unifyCall(calleeType, argTypes, pCall.location());
  }

  private SType unifyCall(SType calleeType, List<SType> argTypes, Location location)
      throws TypeException {
    var resultType = unifier.newFlexibleVar();
    var funcType = new SFuncType(argTypes, resultType);
    try {
      unify(funcType, calleeType);
      return resultType;
    } catch (UnifierException e) {
      throw new TypeException(compileError(location, "Illegal call."), e);
    }
  }

  private SType unifyInstantiate(PInstantiate pInstantiate) throws TypeException {
    var polymorphicP = pInstantiate.polymorphic();
    unifyPolymorphic(polymorphicP);
    var schema = polymorphicP.sSchema();
    pInstantiate.setTypeArgs(generateList(schema.quantifiedVars().size(), unifier::newFlexibleVar));
    return schema.instantiate(pInstantiate.typeArgs());
  }

  private void unifyPolymorphic(PPolymorphic pPolymorphic) throws TypeException {
    switch (pPolymorphic) {
      case PLambda pLambda -> unifyFunc(pLambda);
      case PReference pReference -> unifyReference(pReference);
    }
  }

  private SType unifyNamedArg(PNamedArg pNamedArg) throws TypeException {
    return unifyExpr(pNamedArg.expr());
  }

  private SArrayType unifyOrder(POrder pOrder) throws TypeException {
    var elems = pOrder.elements();
    var elemTypes = elems.map(this::unifyExpr);
    return unifyElementsWithArray(elemTypes, pOrder.location());
  }

  private SArrayType unifyElementsWithArray(List<SType> elemTypes, Location location)
      throws TypeException {
    var elemVar = unifier.newFlexibleVar();
    for (SType elemType : elemTypes) {
      try {
        unify(elemVar, elemType);
      } catch (UnifierException e) {
        throw new TypeException(
            compileError(
                location,
                "Cannot infer type for array literal. Its element types are not compatible."),
            e);
      }
    }
    return new SArrayType(elemVar);
  }

  private void unifyReference(PReference pReference) {
    pReference.setSSchema(typeTeller.schemaFor(pReference.referencedName()));
  }

  private SType unifySelect(PSelect pSelect) throws TypeException {
    var selectableType = unifyExpr(pSelect.selectable());
    if (unifier.resolve(selectableType) instanceof SStructType sStructType) {
      var itemSigS = sStructType.fields().get(pSelect.field());
      if (itemSigS == null) {
        throw new TypeException(
            compileError(pSelect.location(), "Unknown field `" + pSelect.field() + "`."));
      } else {
        return itemSigS.type();
      }
    } else {
      throw new TypeException(compileError(pSelect.location(), "Illegal field access."));
    }
  }

  private void unify(SType sType, SType bodyType) throws UnifierException {
    unifier.add(new Constraint(sType, bodyType));
  }

  private SType translateOrGenerateFlexibleVar(PType pType) {
    if (pType instanceof PImplicitType) {
      return unifier.newFlexibleVar();
    } else {
      return typeTeller.translate(pType);
    }
  }
}
