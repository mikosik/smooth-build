package org.smoothbuild.compile.frontend.compile.ast;

import java.util.List;
import org.smoothbuild.compile.frontend.compile.ast.define.AnnotationP;
import org.smoothbuild.compile.frontend.compile.ast.define.BlobP;
import org.smoothbuild.compile.frontend.compile.ast.define.CallP;
import org.smoothbuild.compile.frontend.compile.ast.define.ExprP;
import org.smoothbuild.compile.frontend.compile.ast.define.FuncP;
import org.smoothbuild.compile.frontend.compile.ast.define.InstantiateP;
import org.smoothbuild.compile.frontend.compile.ast.define.IntP;
import org.smoothbuild.compile.frontend.compile.ast.define.ItemP;
import org.smoothbuild.compile.frontend.compile.ast.define.LambdaP;
import org.smoothbuild.compile.frontend.compile.ast.define.ModuleP;
import org.smoothbuild.compile.frontend.compile.ast.define.NamedArgP;
import org.smoothbuild.compile.frontend.compile.ast.define.NamedEvaluableP;
import org.smoothbuild.compile.frontend.compile.ast.define.NamedFuncP;
import org.smoothbuild.compile.frontend.compile.ast.define.NamedValueP;
import org.smoothbuild.compile.frontend.compile.ast.define.OrderP;
import org.smoothbuild.compile.frontend.compile.ast.define.PolymorphicP;
import org.smoothbuild.compile.frontend.compile.ast.define.ReferenceP;
import org.smoothbuild.compile.frontend.compile.ast.define.ReferenceableP;
import org.smoothbuild.compile.frontend.compile.ast.define.SelectP;
import org.smoothbuild.compile.frontend.compile.ast.define.StringP;
import org.smoothbuild.compile.frontend.compile.ast.define.StructP;
import org.smoothbuild.compile.frontend.compile.ast.define.TypeP;

public class ModuleVisitorP {
  public void visitModule(ModuleP moduleP) {
    visitModuleChildren(moduleP);
  }

  public void visitModuleChildren(ModuleP moduleP) {
    visitStructs(moduleP.structs());
    visitNamedEvaluables(moduleP.evaluables());
  }

  public void visitStructs(List<StructP> structPs) {
    structPs.forEach(this::visitStruct);
  }

  public void visitStruct(StructP structP) {
    visitStructSignature(structP);
  }

  public void visitStructSignature(StructP structP) {
    visitItems(structP.fields());
  }

  public void visitNamedEvaluables(List<NamedEvaluableP> namedEvaluablePs) {
    namedEvaluablePs.forEach(this::visitNamedEvaluable);
  }

  public void visitNamedEvaluable(NamedEvaluableP namedEvaluableP) {
    switch (namedEvaluableP) {
      case NamedFuncP namedFuncP -> visitNamedFunc(namedFuncP);
      case NamedValueP namedValueP -> visitNamedValue(namedValueP);
    }
  }

  public void visitNamedValue(NamedValueP namedValueP) {
    visitNamedValueSignature(namedValueP);
    visitNamedValueBody(namedValueP);
  }

  public void visitNamedValueSignature(NamedValueP namedValueP) {
    namedValueP.annotation().ifPresent(this::visitAnnotation);
    visitType(namedValueP.type());
    visitNameOf(namedValueP);
  }

  public void visitNamedValueBody(NamedValueP namedValueP) {
    namedValueP.body().ifPresent(this::visitExpr);
  }

  public void visitNamedFunc(NamedFuncP namedFuncP) {
    visitNamedFuncSignature(namedFuncP);
    visitFuncBody(namedFuncP);
  }

  public void visitNamedFuncSignature(NamedFuncP namedFuncP) {
    namedFuncP.annotation().ifPresent(this::visitAnnotation);
    visitType(namedFuncP.resultT());
    visitItems(namedFuncP.params());
    visitNameOf(namedFuncP);
  }

  public void visitFuncBody(FuncP funcP) {
    funcP.body().ifPresent(expr -> visitFuncBody(funcP, expr));
  }

  public void visitFuncBody(FuncP funcP, ExprP exprP) {
    visitExpr(exprP);
  }

  public void visitAnnotation(AnnotationP annotationP) {
    visitString(annotationP.value());
  }

  public void visitItems(List<ItemP> itemPs) {
    itemPs.forEach(this::visitItem);
  }

  public void visitItem(ItemP itemP) {
    visitType(itemP.type());
    visitNameOf(itemP);
    itemP.defaultValue().ifPresent(this::visitNamedValue);
  }

  public void visitType(TypeP typeP) {}

  public void visitExpr(ExprP exprP) {
    switch (exprP) {
      case BlobP blobP -> visitBlob(blobP);
      case CallP callP -> visitCall(callP);
      case IntP intP -> visitInt(intP);
      case InstantiateP instantiateP -> visitInstantiateP(instantiateP);
      case NamedArgP namedArgP -> visitNamedArg(namedArgP);
      case OrderP orderP -> visitOrder(orderP);
      case SelectP selectP -> visitSelect(selectP);
      case StringP stringP -> visitString(stringP);
    }
  }

  public void visitLambda(LambdaP lambdaP) {
    visitLambdaSignature(lambdaP);
    visitFuncBody(lambdaP);
  }

  public void visitLambdaSignature(LambdaP lambdaP) {
    visitType(lambdaP.resultT());
    visitItems(lambdaP.params());
  }

  public void visitArgs(List<ExprP> args) {
    args.forEach(this::visitArg);
  }

  public void visitArg(ExprP arg) {
    visitExpr(arg);
  }

  public void visitBlob(BlobP blobP) {}

  public void visitCall(CallP callP) {
    visitExpr(callP.callee());
    visitArgs(callP.args());
  }

  public void visitInt(IntP intP) {}

  private void visitPolymorphicP(PolymorphicP polymorphicP) {
    switch (polymorphicP) {
      case LambdaP lambdaP -> visitLambda(lambdaP);
      case ReferenceP referenceP -> visitReference(referenceP);
    }
  }

  public void visitInstantiateP(InstantiateP instantiateP) {
    visitPolymorphicP(instantiateP.polymorphic());
  }

  public void visitNamedArg(NamedArgP namedArgP) {
    visitExpr(namedArgP.expr());
  }

  public void visitOrder(OrderP orderP) {
    orderP.elems().forEach(this::visitExpr);
  }

  public void visitReference(ReferenceP referenceP) {}

  public void visitSelect(SelectP selectP) {
    visitExpr(selectP.selectable());
  }

  public void visitString(StringP stringP) {}

  public void visitNameOf(ReferenceableP referenceableP) {}
}
