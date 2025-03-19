package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.base.Throwables.unexpectedCaseException;
import static org.smoothbuild.common.collect.List.generateList;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.compilerfrontend.compile.task.CompileError.compileError;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.compile.ast.define.PBlob;
import org.smoothbuild.compilerfrontend.compile.ast.define.PCall;
import org.smoothbuild.compilerfrontend.compile.ast.define.PEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExplicitType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInstantiate;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInt;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedArg;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.POrder;
import org.smoothbuild.compilerfrontend.compile.ast.define.PSelect;
import org.smoothbuild.compilerfrontend.compile.ast.define.PString;
import org.smoothbuild.compilerfrontend.compile.ast.define.PType;
import org.smoothbuild.compilerfrontend.lang.base.MonoReferenceable;
import org.smoothbuild.compilerfrontend.lang.base.PolyEvaluable;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypes;
import org.smoothbuild.compilerfrontend.lang.type.tool.Constraint;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;
import org.smoothbuild.compilerfrontend.lang.type.tool.UnifierException;

public class ConstraintCollector {
  private final Unifier unifier;

  private ConstraintCollector(Unifier unifier) {
    this.unifier = unifier;
  }

  public static void collectConstraints(Unifier unifier, PEvaluable pEvaluable)
      throws TypeException {
    new ConstraintCollector(unifier).unifyEvaluable(pEvaluable);
  }

  private void unifyEvaluable(PEvaluable pEvaluable) throws TypeException {
    switch (pEvaluable) {
      case PNamedValue pNamedValue -> unifyNamedValue(pNamedValue);
      case PFunc pNamedFunc -> unifyFunc(pNamedFunc);
    }
  }

  private void unifyNamedValue(PNamedValue pNamedValue) throws TypeException {
    generateSType(pNamedValue.pType());
    unifyEvaluableBody(pNamedValue);
  }

  private void unifyFunc(PFunc pFunc) throws TypeException {
    pFunc.params().forEach(p -> generateSType(p.pType()));
    generateSType(pFunc.resultType());
    unifyEvaluableBody(pFunc);
  }

  private void unifyEvaluableBody(PEvaluable pEvaluable) throws TypeException {
    var evaluationType = pEvaluable.evaluationType().sType();
    new ConstraintCollector(unifier).unifyEvaluableBody(pEvaluable, evaluationType);
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
              pEvaluable.q() + " body type " + unifier.resolve(bodyType).q()
                  + " is not assignable to declared type " + evaluationType.q() + "."),
          e);
    }
  }

  private SType unifyExpr(PExpr pExpr) throws TypeException {
    return switch (pExpr) {
      case PCall pCall -> unifyAndMemoize(pCall, this::unifyCall);
      case PInstantiate pInstantiate -> unifyAndMemoize(pInstantiate, this::unifyInstantiate);
      case PLambda pLambda -> unifyLambda(pLambda);
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
    var resultType = unifier.newFlexibleTypeVar();
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

  private SType unifyInstantiate(PInstantiate pInstantiate) {
    var pReference = pInstantiate.reference();
    return switch (pReference.referenced()) {
      case MonoReferenceable mono -> {
        pInstantiate.setTypeArgs(list());
        yield mono.type();
      }
      case PolyEvaluable poly -> {
        var argSize = poly.typeParams().size();
        List<SType> typeArgs = generateList(argSize, unifier::newFlexibleTypeVar);
        pInstantiate.setTypeArgs(typeArgs);
        yield poly.instantiatedType(typeArgs);
      }
      default -> throw unexpectedCaseException(pReference.referenced());
    };
  }

  private SFuncType unifyLambda(PLambda pLambda) throws TypeException {
    pLambda.params().forEach(p -> generateSType(p.pType()));
    generateSType(pLambda.resultType());
    unifyEvaluableBody(pLambda, pLambda.evaluationType().sType());
    return pLambda.type();
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
    var elemVar = unifier.newFlexibleTypeVar();
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

  private void unify(SType sType1, SType sType2) throws UnifierException {
    unifier.add(new Constraint(sType1, sType2));
  }

  private void generateSType(PType pType) {
    pType.setSType(
        pType instanceof PExplicitType explicit ? explicit.infer() : unifier.newFlexibleTypeVar());
  }
}
