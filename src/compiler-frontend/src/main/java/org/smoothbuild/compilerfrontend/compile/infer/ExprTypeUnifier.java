package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.collect.List.generateList;
import static org.smoothbuild.compilerfrontend.compile.task.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.sVarSet;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.compile.ast.define.PBlob;
import org.smoothbuild.compilerfrontend.compile.ast.define.PCall;
import org.smoothbuild.compilerfrontend.compile.ast.define.PEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExplicitTypeParams;
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
import org.smoothbuild.compilerfrontend.compile.ast.define.PScope;
import org.smoothbuild.compilerfrontend.compile.ast.define.PSelect;
import org.smoothbuild.compilerfrontend.compile.ast.define.PString;
import org.smoothbuild.compilerfrontend.compile.ast.define.PType;
import org.smoothbuild.compilerfrontend.lang.name.NList;
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
  private final PScope scope;
  private final SVarSet outerScopeVars;

  private ExprTypeUnifier(Unifier unifier, PScope scope) {
    this(unifier, scope, sVarSet());
  }

  private ExprTypeUnifier(Unifier unifier, PScope scope, SVarSet outerScopeVars) {
    this.unifier = unifier;
    this.scope = scope;
    this.outerScopeVars = outerScopeVars;
  }

  public static void unifyNamedValue(Unifier unifier, PScope scope, PNamedValue pNamedValue)
      throws TypeException {
    new ExprTypeUnifier(unifier, scope).unifyNamedValue(pNamedValue);
  }

  private void unifyNamedValue(PNamedValue pNamedValue) throws TypeException {
    var evaluationType = translateOrGenerateFlexibleVar(pNamedValue.evaluationType());
    pNamedValue.setSType(evaluationType);
    unifyEvaluableBody(pNamedValue, evaluationType, scope);
    var resolvedType = resolveType(pNamedValue);
    var typeParams = resolveTypeParams(resolvedType);
    verifyInferredTypeParamsAreEqualToExplicitlyDeclared(pNamedValue, typeParams);
    pNamedValue.setSchema(new SSchema(typeParams, resolvedType));
  }

  public static void unifyFunc(Unifier unifier, PScope scope, PFunc pFunc) throws TypeException {
    new ExprTypeUnifier(unifier, scope).unifyFunc(pFunc);
  }

  private void unifyFunc(PFunc pFunc) throws TypeException {
    var paramTypes = inferParamTypes(pFunc.params());
    var resultType = translateOrGenerateFlexibleVar(pFunc.resultT());
    pFunc.setSType(new SFuncType(paramTypes, resultType));
    unifyEvaluableBody(pFunc, resultType, pFunc.scope());
    var resolvedT = resolveType(pFunc);
    var typeParams = resolveTypeParams(resolvedT);
    verifyInferredTypeParamsAreEqualToExplicitlyDeclared(pFunc, typeParams);
    pFunc.setSchema(new SFuncSchema(typeParams, (SFuncType) resolvedT));
  }

  private static void verifyInferredTypeParamsAreEqualToExplicitlyDeclared(
      PEvaluable pEvaluable, SVarSet inferredTypeParams) throws TypeException {
    if (pEvaluable.typeParams() instanceof PExplicitTypeParams explicitTypeParams) {
      var explicit = explicitTypeParams.toVarSet();
      if (!explicit.equals(inferredTypeParams)) {
        throw new TypeException(compileError(
            explicitTypeParams.location(),
            "Type parameters are declared as " + explicit.q() + " but inferred type parameters are "
                + inferredTypeParams.q() + "."));
      }
    }
  }

  private SVarSet resolveTypeParams(SType sType) {
    return sType.vars().removeAll(outerScopeVars.map(unifier::resolve));
  }

  private SType resolveType(PEvaluable pEvaluable) {
    return unifier.resolve(pEvaluable.sType());
  }

  private List<SType> inferParamTypes(NList<PItem> params) {
    var paramTypes = params.list().map(p -> scope.translate(p.type()));
    params.list().zip(paramTypes, PItem::setSType);
    return paramTypes;
  }

  private void unifyEvaluableBody(PEvaluable pEvaluable, SType evaluationType, PScope bodyScope)
      throws TypeException {
    var vars = outerScopeVars.addAll(pEvaluable.typeParams().toVarSet());
    new ExprTypeUnifier(unifier, bodyScope, vars).unifyEvaluableBody(pEvaluable, evaluationType);
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
              pEvaluable.location(),
              pEvaluable.q() + " body type " + bodyType.q()
                  + " is not equal to declared type " + evaluationType.q()
                  + "."),
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
    var resolvedCalleeType = unifier.resolve(calleeType);
    var resolvedArgTypes = argTypes.map(unifier::resolve);
    try {
      unify(funcType, calleeType);
      return resultType;
    } catch (UnifierException e) {
      throw new TypeException(
          compileError(
              location,
              "Illegal call: Instance of " + resolvedCalleeType.q()
                  + " cannot be called with arguments `"
                  + resolvedArgTypes.map(SType::specifier).toString("(", ", ", ")")
                  + "`."),
          e);
    }
  }

  private SType unifyInstantiate(PInstantiate pInstantiate) throws TypeException {
    var polymorphicP = pInstantiate.polymorphic();
    unifyPolymorphic(polymorphicP);
    var schema = polymorphicP.schema();
    pInstantiate.setTypeArgs(generateList(schema.typeParams().size(), unifier::newFlexibleVar));
    return schema.instantiate(pInstantiate.typeArgs());
  }

  private void unifyPolymorphic(PPolymorphic pPolymorphic) throws TypeException {
    switch (pPolymorphic) {
      case PLambda pLambda -> unifyLambda(pLambda);
      case PReference pReference -> unifyReference(pReference);
    }
  }

  private void unifyLambda(PLambda pLambda) throws TypeException {
    new ExprTypeUnifier(unifier, pLambda.scope(), outerScopeVars).unifyFunc(pLambda);
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
    for (int i = 0; i < elemTypes.size(); i++) {
      SType elemType = elemTypes.get(i);
      try {
        unify(elemVar, elemType);
      } catch (UnifierException e) {
        throw new TypeException(
            compileError(
                location,
                "Cannot infer array type. After unifying first " + (i + 1)
                    + " elements, array type is inferred as "
                    + new SArrayType(unifier.resolve(elemVar)).q()
                    + ". However type of element at index " + i + " is "
                    + unifier.resolve(elemType).q() + "."),
            e);
      }
    }
    return new SArrayType(elemVar);
  }

  private void unifyReference(PReference pReference) {
    pReference.setSSchema(scope.schemaFor(pReference.id()));
  }

  private SType unifySelect(PSelect pSelect) throws TypeException {
    var selectableType = unifyExpr(pSelect.selectable());
    var resolvedSelectableType = unifier.resolve(selectableType);
    if (resolvedSelectableType instanceof SStructType sStructType) {
      var itemSigS = sStructType.fields().get(pSelect.fieldName());
      if (itemSigS == null) {
        throw new TypeException(compileError(
            pSelect.location(),
            "Struct " + sStructType.fqn().q() + " has no field "
                + pSelect.fieldName().q() + "."));
      } else {
        return itemSigS.type();
      }
    } else {
      throw new TypeException(compileError(
          pSelect.location(),
          "Instance of " + resolvedSelectableType.q()
              + " has no field " + pSelect.fieldName().q()
              + "."));
    }
  }

  private void unify(SType sType, SType bodyType) throws UnifierException {
    unifier.add(new Constraint(sType, bodyType));
  }

  private SType translateOrGenerateFlexibleVar(PType pType) {
    if (pType instanceof PImplicitType) {
      return unifier.newFlexibleVar();
    } else {
      return scope.translate(pType);
    }
  }
}
