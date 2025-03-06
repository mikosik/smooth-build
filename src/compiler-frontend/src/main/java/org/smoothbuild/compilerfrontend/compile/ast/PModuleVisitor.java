package org.smoothbuild.compilerfrontend.compile.ast;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.compile.ast.define.PAnnotation;
import org.smoothbuild.compilerfrontend.compile.ast.define.PBlob;
import org.smoothbuild.compilerfrontend.compile.ast.define.PCall;
import org.smoothbuild.compilerfrontend.compile.ast.define.PContainer;
import org.smoothbuild.compilerfrontend.compile.ast.define.PEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExplicitTypeParams;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PImplicitTypeParams;
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
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReferenceable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PSelect;
import org.smoothbuild.compilerfrontend.compile.ast.define.PString;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.compile.ast.define.PType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PTypeParam;
import org.smoothbuild.compilerfrontend.compile.ast.define.PTypeParams;

public abstract class PModuleVisitor<T extends Throwable> {
  public PModuleVisitor() {}

  public void visit(List<? extends PContainer> pContainers) throws T {
    pContainers.foreach(this::visit);
  }

  public void visit(PContainer pContainer) throws T {
    switch (pContainer) {
      case PModule pModule -> visitModule(pModule);
      case PEvaluable pEvaluable -> visitEvaluable(pEvaluable);
      case PStruct pStruct -> visitStruct(pStruct);
    }
  }

  public void visitEvaluable(PEvaluable pEvaluable) throws T {
    switch (pEvaluable) {
      case PLambda pLambda -> visitLambda(pLambda);
      case PNamedEvaluable pNamedEvaluable -> visitNamedEvaluable(pNamedEvaluable);
    }
  }

  public void visitNamedEvaluable(PNamedEvaluable pNamedEvaluable) throws T {
    switch (pNamedEvaluable) {
      case PNamedFunc pNamedFunc -> visitNamedFunc(pNamedFunc);
      case PNamedValue pNamedValue -> visitNamedValue(pNamedValue);
    }
  }

  public void visitModule(PModule pModule) throws T {
    visitModuleChildren(pModule);
  }

  public void visitModuleChildren(PModule pModule) throws T {
    visit(pModule.structs());
    visit(pModule.evaluables());
  }

  public void visitStruct(PStruct pStruct) throws T {
    visitStructSignature(pStruct);
  }

  public void visitStructSignature(PStruct pStruct) throws T {
    visitItems(pStruct.fields().list());
    visitNameOf(pStruct);
  }

  public void visitNamedValue(PNamedValue pNamedValue) throws T {
    visitNamedValueSignature(pNamedValue);
    visitNamedValueBody(pNamedValue);
  }

  public void visitNamedValueSignature(PNamedValue pNamedValue) throws T {
    pNamedValue.annotation().ifPresent(this::visitAnnotation);
    visitType(pNamedValue.type());
    visitTypeParams(pNamedValue.pTypeParams());
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
    visitType(pNamedFunc.resultType());
    visitTypeParams(pNamedFunc.pTypeParams());
    visitItems(pNamedFunc.params().list());
    visitNameOf(pNamedFunc);
  }

  public void visitTypeParams(PTypeParams pExplicitTypeParams) {
    switch (pExplicitTypeParams) {
      case PExplicitTypeParams explicit -> explicit.typeParams().foreach(this::visitTypeParam);
      case PImplicitTypeParams implicit -> {}
    }
  }

  public void visitTypeParam(PTypeParam pTypeParam) {}

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
    pItems.foreach(this::visitItem);
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
      case PLambda pLambda -> visit(pLambda);
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
    visitType(pLambda.resultType());
    visitTypeParams(pLambda.pTypeParams());
    visitItems(pLambda.params().list());
    visitNameOf(pLambda);
  }

  public void visitArgs(List<PExpr> args) throws T {
    args.foreach(this::visitArg);
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

  public void visitInstantiate(PInstantiate pInstantiate) throws T {
    visitReference(pInstantiate.reference());
  }

  public void visitNamedArg(PNamedArg pNamedArg) throws T {
    visitExpr(pNamedArg.expr());
  }

  public void visitOrder(POrder pOrder) throws T {
    pOrder.elements().foreach(this::visitExpr);
  }

  public void visitReference(PReference pReference) throws T {}

  public void visitSelect(PSelect pSelect) throws T {
    visitExpr(pSelect.selectable());
  }

  public void visitString(PString pString) throws T {}

  public void visitNameOf(PReferenceable pReferenceable) throws T {}

  public void visitNameOf(PLambda pLambda) throws T {}

  public void visitNameOf(PStruct pStruct) throws T {}
}
