package org.smoothbuild.compilerfrontend.compile.infer;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.compilerfrontend.compile.ast.define.PBlob;
import org.smoothbuild.compilerfrontend.compile.ast.define.PCall;
import org.smoothbuild.compilerfrontend.compile.ast.define.PCombine;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInstantiate;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInt;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedArg;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.POrder;
import org.smoothbuild.compilerfrontend.compile.ast.define.PString;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStructSelect;
import org.smoothbuild.compilerfrontend.compile.ast.define.PTupleSelect;
import org.smoothbuild.compilerfrontend.compile.ast.define.PType;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;

public class TypeAssigner {
  private final Unifier unifier;

  private TypeAssigner(Unifier unifier) {
    this.unifier = unifier;
  }

  public static void assignInferredTypes(Unifier unifier, PNamedEvaluable pNamedEvaluable)
      throws TypeException {
    new TypeAssigner(unifier).handleNamedEvaluable(pNamedEvaluable);
  }

  private void handleNamedEvaluable(PNamedEvaluable pNamedEvaluable) throws TypeException {
    switch (pNamedEvaluable) {
      case PNamedValue pNamedValue -> assignNamedValue(pNamedValue);
      case PFunc pFunc -> assignFunc(pFunc);
    }
  }

  private void assignNamedValue(PNamedValue pNamedValue) throws TypeException {
    assignBody(pNamedValue.body());
    assignType(pNamedValue.pType());
  }

  private void assignFunc(PFunc pFunc) throws TypeException {
    assignBody(pFunc.body());
    assignType(pFunc.resultType());
    pFunc.params().forEach(p -> assignType(p.pType()));
  }

  private void assignType(PType pType) {
    pType.setSType(unifier.resolve(pType.sType()));
  }

  private void assignBody(Maybe<PExpr> body) throws TypeException {
    body.ifPresent(this::assignBody);
  }

  private void assignBody(PExpr body) throws TypeException {
    assignExpr(body);
  }

  private void assignExpr(PExpr expr) throws TypeException {
    switch (expr) {
      case PCall pCall -> assignCall(pCall);
      case PCombine pCombine -> assignCombine(pCombine);
      case PInstantiate pInstantiate -> assignInstantiate(pInstantiate);
      case PLambda pLambda -> assignFunc(pLambda);
      case PNamedArg pNamedArg -> assignNamedArg(pNamedArg);
      case POrder pOrder -> assignOrder(pOrder);
      case PStructSelect pStructSelect -> assignStructSelect(pStructSelect);
      case PTupleSelect pTupleSelect -> assignTupleSelect(pTupleSelect);
      case PString pString -> assignExprType(pString);
      case PInt pInt -> assignExprType(pInt);
      case PBlob pBlob -> assignExprType(pBlob);
    }
  }

  private void assignCall(PCall pCall) throws TypeException {
    assignExpr(pCall.callee());
    pCall.positionedArgs().foreach(this::assignExpr);
    assignExprType(pCall);
  }

  private void assignInstantiate(PInstantiate pInstantiate) {
    pInstantiate.setTypeArgs(pInstantiate.typeArgs().map(unifier::resolve));
  }

  private void assignNamedArg(PNamedArg pNamedArg) throws TypeException {
    assignExpr(pNamedArg.expr());
    assignExprType(pNamedArg);
  }

  private void assignOrder(POrder pOrder) throws TypeException {
    pOrder.elements().foreach(this::assignExpr);
    assignExprType(pOrder);
  }

  private void assignCombine(PCombine pCombine) throws TypeException {
    pCombine.elements().foreach(this::assignExpr);
    assignExprType(pCombine);
  }

  private void assignStructSelect(PStructSelect pStructSelect) throws TypeException {
    assignExpr(pStructSelect.selectable());
    assignExprType(pStructSelect);
  }

  private void assignTupleSelect(PTupleSelect pTupleSelect) throws TypeException {
    assignExpr(pTupleSelect.selectable());
    assignExprType(pTupleSelect);
  }

  private void assignExprType(PExpr pExpr) {
    pExpr.setSType(unifier.resolve(pExpr.sType()));
  }
}
