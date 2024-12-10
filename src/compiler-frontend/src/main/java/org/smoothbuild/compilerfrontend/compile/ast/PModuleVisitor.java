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

public class PModuleVisitor<T extends Throwable> {
  public void visitModule(PModule pModule) throws T {
    visitModuleChildren(pModule);
  }

  public void visitModuleChildren(PModule pModule) throws T {
    visitStructs(pModule.structs());
    visitNamedEvaluables(pModule.evaluables());
  }

  public void visitStructs(List<PStruct> pStructs) throws T {
    for (var pStruct : pStructs) {
      visitStruct(pStruct);
    }
  }

  public void visitStruct(PStruct pStruct) throws T {
    visitStructSignature(pStruct);
  }

  public void visitStructSignature(PStruct pStruct) throws T {
    visitItems(pStruct.fields());
  }

  public void visitNamedEvaluables(List<PNamedEvaluable> pNamedEvaluables) throws T {
    for (var pNamedEvaluable : pNamedEvaluables) {
      visitNamedEvaluable(pNamedEvaluable);
    }
  }

  public void visitNamedEvaluable(PNamedEvaluable pNamedEvaluable) throws T {
    switch (pNamedEvaluable) {
      case PNamedFunc pNamedFunc -> visitNamedFunc(pNamedFunc);
      case PNamedValue pNamedValue -> visitNamedValue(pNamedValue);
    }
  }

  public void visitNamedValue(PNamedValue pNamedValue) throws T {
    visitNamedValueSignature(pNamedValue);
    visitNamedValueBody(pNamedValue);
  }

  public void visitNamedValueSignature(PNamedValue pNamedValue) throws T {
    pNamedValue.annotation().ifPresent(this::visitAnnotation);
    visitType(pNamedValue.type());
    visitNameOf(pNamedValue);
  }

  public void visitNamedValueBody(PNamedValue pNamedValue) throws T {
    pNamedValue.body().ifPresent(this::visitExpr);
  }

  public void visitNamedFunc(PNamedFunc pNamedFunc) throws T {
    visitNamedFuncSignature(pNamedFunc);
    visitFuncBody(pNamedFunc);
  }

  public void visitNamedFuncSignature(PNamedFunc pNamedFunc) throws T {
    pNamedFunc.annotation().ifPresent(this::visitAnnotation);
    visitType(pNamedFunc.resultT());
    visitItems(pNamedFunc.params());
    visitNameOf(pNamedFunc);
  }

  public void visitFuncBody(PFunc pFunc) throws T {
    pFunc.body().ifPresent(expr -> visitFuncBody(pFunc, expr));
  }

  public void visitFuncBody(PFunc pFunc, PExpr pExpr) throws T {
    visitExpr(pExpr);
  }

  public void visitAnnotation(PAnnotation pAnnotation) throws T {
    visitString(pAnnotation.value());
  }

  public void visitItems(List<PItem> pItems) throws T {
    for (var pItem : pItems) {
      visitItem(pItem);
    }
  }

  public void visitItem(PItem pItem) throws T {
    visitType(pItem.type());
    visitNameOf(pItem);
  }

  public void visitType(PType pType) throws T {}

  public void visitExpr(PExpr pExpr) throws T {
    switch (pExpr) {
      case PBlob pBlob -> visitBlob(pBlob);
      case PCall pCall -> visitCall(pCall);
      case PInt pInt -> visitInt(pInt);
      case PInstantiate pInstantiate -> visitInstantiate(pInstantiate);
      case PNamedArg pNamedArg -> visitNamedArg(pNamedArg);
      case POrder pOrder -> visitOrder(pOrder);
      case PSelect pSelect -> visitSelect(pSelect);
      case PString pString -> visitString(pString);
    }
  }

  public void visitLambda(PLambda pLambda) throws T {
    visitLambdaSignature(pLambda);
    visitFuncBody(pLambda);
  }

  public void visitLambdaSignature(PLambda pLambda) throws T {
    visitType(pLambda.resultT());
    visitItems(pLambda.params());
  }

  public void visitArgs(List<PExpr> args) throws T {
    for (var arg : args) {
      visitArg(arg);
    }
  }

  public void visitArg(PExpr arg) throws T {
    visitExpr(arg);
  }

  public void visitBlob(PBlob pBlob) throws T {}

  public void visitCall(PCall pCall) throws T {
    visitExpr(pCall.callee());
    visitArgs(pCall.args());
  }

  public void visitInt(PInt pInt) throws T {}

  private void visitPolymorphicP(PPolymorphic pPolymorphic) throws T {
    switch (pPolymorphic) {
      case PLambda pLambda -> visitLambda(pLambda);
      case PReference pReference -> visitReference(pReference);
    }
  }

  public void visitInstantiate(PInstantiate pInstantiate) throws T {
    visitPolymorphicP(pInstantiate.polymorphic());
  }

  public void visitNamedArg(PNamedArg pNamedArg) throws T {
    visitExpr(pNamedArg.expr());
  }

  public void visitOrder(POrder pOrder) throws T {
    pOrder.elements().withEach(this::visitExpr);
  }

  public void visitReference(PReference pReference) throws T {}

  public void visitSelect(PSelect pSelect) throws T {
    visitExpr(pSelect.selectable());
  }

  public void visitString(PString pString) throws T {}

  public void visitNameOf(PReferenceable pReferenceable) throws T {}
}
