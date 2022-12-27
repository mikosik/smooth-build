package org.smoothbuild.compile.ps.ast;

import java.util.List;

import org.smoothbuild.compile.ps.ast.expr.AnnotationP;
import org.smoothbuild.compile.ps.ast.expr.AnonymousFuncP;
import org.smoothbuild.compile.ps.ast.expr.BlobP;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.FuncP;
import org.smoothbuild.compile.ps.ast.expr.IntP;
import org.smoothbuild.compile.ps.ast.expr.ItemP;
import org.smoothbuild.compile.ps.ast.expr.ModuleP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.NamedEvaluableP;
import org.smoothbuild.compile.ps.ast.expr.NamedFuncP;
import org.smoothbuild.compile.ps.ast.expr.NamedValueP;
import org.smoothbuild.compile.ps.ast.expr.OrderP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.RefableP;
import org.smoothbuild.compile.ps.ast.expr.SelectP;
import org.smoothbuild.compile.ps.ast.expr.StringP;
import org.smoothbuild.compile.ps.ast.expr.StructP;
import org.smoothbuild.compile.ps.ast.type.TypeP;

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
      case NamedFuncP func -> visitNamedFunc(func);
      case NamedValueP value -> visitNamedValue(value);
    }
  }

  public void visitNamedValue(NamedValueP namedValueP) {
    visitNamedValueSignature(namedValueP);
    visitNamedValueBody(namedValueP);
  }

  public void visitNamedValueSignature(NamedValueP namedValueP) {
    namedValueP.annotation().ifPresent(this::visitAnnotation);
    namedValueP.type().ifPresent(this::visitType);
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
    namedFuncP.resT().ifPresent(this::visitType);
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
    // @formatter:off
    switch (exprP) {
      case BlobP          blobP          -> visitBlob(blobP);
      case CallP          callP          -> visitCall(callP);
      case IntP           intP           -> visitInt(intP);
      case AnonymousFuncP anonymousFuncP -> visitAnonymousFunc(anonymousFuncP);
      case NamedArgP      namedArgP      -> visitNamedArg(namedArgP);
      case OrderP         orderP         -> visitOrder(orderP);
      case RefP           refP           -> visitRef(refP);
      case SelectP        selectP        -> visitSelect(selectP);
      case StringP        stringP        -> visitString(stringP);
    }
    // @formatter:on
  }

  public void visitArgs(List<ExprP> args) {
    args.forEach(this::visitArg);
  }

  public void visitArg(ExprP arg) {
    visitExpr(arg);
  }

  public void visitBlob(BlobP blobP) {
  }

  public void visitCall(CallP callP) {
    visitExpr(callP.callee());
    visitArgs(callP.args());
  }

  public void visitInt(IntP intP) {
  }

  public void visitAnonymousFunc(AnonymousFuncP anonymousFuncP) {
    visitAnonymousFuncSignature(anonymousFuncP);
    visitFuncBody(anonymousFuncP);
  }

  public void visitAnonymousFuncSignature(AnonymousFuncP anonymousFuncP) {
    visitItems(anonymousFuncP.params());
  }

  public void visitNamedArg(NamedArgP namedArg) {
    visitExpr(namedArg.expr());
  }

  public void visitOrder(OrderP orderP) {
    orderP.elems().forEach(this::visitExpr);
  }

  public void visitSelect(SelectP selectP) {
    visitExpr(selectP.selectable());
  }

  public void visitRef(RefP refP) {}

  public void visitString(StringP stringP) {}

  public void visitNameOf(RefableP refableP) {}
}