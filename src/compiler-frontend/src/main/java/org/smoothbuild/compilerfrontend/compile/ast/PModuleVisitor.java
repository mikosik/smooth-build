package org.smoothbuild.compilerfrontend.compile.ast;

import java.util.List;
import org.smoothbuild.compilerfrontend.compile.ast.define.PAnnotation;
import org.smoothbuild.compilerfrontend.compile.ast.define.PBlob;
import org.smoothbuild.compilerfrontend.compile.ast.define.PCall;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInstantiate;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInt;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedArg;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.POrder;
import org.smoothbuild.compilerfrontend.compile.ast.define.PPolymorphic;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReferenceable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PSelect;
import org.smoothbuild.compilerfrontend.compile.ast.define.PString;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.compile.ast.define.PType;

public class PModuleVisitor {
  public void visitModule(PModule pModule) {
    visitModuleChildren(pModule);
  }

  public void visitModuleChildren(PModule pModule) {
    visitStructs(pModule.structs());
    visitNamedEvaluables(pModule.evaluables());
  }

  public void visitStructs(List<PStruct> pStructs) {
    pStructs.forEach(this::visitStruct);
  }

  public void visitStruct(PStruct pStruct) {
    visitStructSignature(pStruct);
  }

  public void visitStructSignature(PStruct pStruct) {
    visitItems(pStruct.fields());
  }

  public void visitNamedEvaluables(List<PNamedEvaluable> pNamedEvaluable) {
    pNamedEvaluable.forEach(this::visitNamedEvaluable);
  }

  public void visitNamedEvaluable(PNamedEvaluable pNamedEvaluable) {
    switch (pNamedEvaluable) {
      case PNamedFunc pNamedFunc -> visitNamedFunc(pNamedFunc);
      case PNamedValue pNamedValue -> visitNamedValue(pNamedValue);
    }
  }

  public void visitNamedValue(PNamedValue pNamedValue) {
    visitNamedValueSignature(pNamedValue);
    visitNamedValueBody(pNamedValue);
  }

  public void visitNamedValueSignature(PNamedValue pNamedValue) {
    pNamedValue.annotation().ifPresent(this::visitAnnotation);
    visitType(pNamedValue.type());
    visitNameOf(pNamedValue);
  }

  public void visitNamedValueBody(PNamedValue pNamedValue) {
    pNamedValue.body().ifPresent(this::visitExpr);
  }

  public void visitNamedFunc(PNamedFunc pNamedFunc) {
    visitNamedFuncSignature(pNamedFunc);
    visitFuncBody(pNamedFunc);
  }

  public void visitNamedFuncSignature(PNamedFunc pNamedFunc) {
    pNamedFunc.annotation().ifPresent(this::visitAnnotation);
    visitType(pNamedFunc.resultT());
    visitItems(pNamedFunc.params());
    visitNameOf(pNamedFunc);
  }

  public void visitFuncBody(PFunc pFunc) {
    pFunc.body().ifPresent(expr -> visitFuncBody(pFunc, expr));
  }

  public void visitFuncBody(PFunc pFunc, PExpr pExpr) {
    visitExpr(pExpr);
  }

  public void visitAnnotation(PAnnotation pAnnotation) {
    visitString(pAnnotation.value());
  }

  public void visitItems(List<PItem> pItems) {
    pItems.forEach(this::visitItem);
  }

  public void visitItem(PItem pItem) {
    visitType(pItem.type());
    visitNameOf(pItem);
    pItem.defaultValue().ifPresent(this::visitNamedValue);
  }

  public void visitType(PType pType) {}

  public void visitExpr(PExpr pExpr) {
    switch (pExpr) {
      case PBlob pBlob -> visitBlob(pBlob);
      case PCall pCall -> visitCall(pCall);
      case PInt pInt -> visitInt(pInt);
      case PInstantiate pInstantiate -> visitInstantiateP(pInstantiate);
      case PNamedArg pNamedArg -> visitNamedArg(pNamedArg);
      case POrder pOrder -> visitOrder(pOrder);
      case PSelect pSelect -> visitSelect(pSelect);
      case PString pString -> visitString(pString);
    }
  }

  public void visitLambda(PLambda pLambda) {
    visitLambdaSignature(pLambda);
    visitFuncBody(pLambda);
  }

  public void visitLambdaSignature(PLambda pLambda) {
    visitType(pLambda.resultT());
    visitItems(pLambda.params());
  }

  public void visitArgs(List<PExpr> args) {
    args.forEach(this::visitArg);
  }

  public void visitArg(PExpr arg) {
    visitExpr(arg);
  }

  public void visitBlob(PBlob pBlob) {}

  public void visitCall(PCall pCall) {
    visitExpr(pCall.callee());
    visitArgs(pCall.args());
  }

  public void visitInt(PInt pInt) {}

  private void visitPolymorphicP(PPolymorphic pPolymorphic) {
    switch (pPolymorphic) {
      case PLambda pLambda -> visitLambda(pLambda);
      case PReference pReference -> visitReference(pReference);
    }
  }

  public void visitInstantiateP(PInstantiate pInstantiate) {
    visitPolymorphicP(pInstantiate.polymorphic());
  }

  public void visitNamedArg(PNamedArg pNamedArg) {
    visitExpr(pNamedArg.expr());
  }

  public void visitOrder(POrder pOrder) {
    pOrder.elements().forEach(this::visitExpr);
  }

  public void visitReference(PReference pReference) {}

  public void visitSelect(PSelect pSelect) {
    visitExpr(pSelect.selectable());
  }

  public void visitString(PString pString) {}

  public void visitNameOf(PReferenceable pReferenceable) {}
}
