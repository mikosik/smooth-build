package org.smoothbuild.compile.ps.ast;

import java.util.List;
import java.util.function.BiConsumer;

import org.smoothbuild.compile.ps.ast.expr.AnnotationP;
import org.smoothbuild.compile.ps.ast.expr.AnonymousFuncP;
import org.smoothbuild.compile.ps.ast.expr.BlobP;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.FuncP;
import org.smoothbuild.compile.ps.ast.expr.IntP;
import org.smoothbuild.compile.ps.ast.expr.ItemP;
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

public class AstVisitor {
  public void visitAst(Ast ast) {
    visitStructs(ast.structs());
    visitNamedEvaluable(ast.evaluables());
  }

  public void visitStructs(List<StructP> structs) {
    structs.forEach(this::visitStruct);
  }

  public void visitStruct(StructP struct) {
    visitFields(struct.fields());
  }

  public void visitFields(List<ItemP> fields) {
    fields.forEach(this::visitField);
  }

  public void visitField(ItemP field) {
    visitType(field.type());
    visitNameOf(field);
  }

  public void visitNamedEvaluable(List<NamedEvaluableP> namedEvaluable) {
    namedEvaluable.forEach(this::visitNamedEvaluable);
  }

  public void visitNamedEvaluable(NamedEvaluableP namedEvaluable) {
    switch (namedEvaluable) {
      case NamedFuncP func -> visitNamedFunc(func);
      case NamedValueP value -> visitNamedValue(value);
    }
  }

  public void visitNamedValue(NamedValueP namedValueP) {
    namedValueP.annotation().ifPresent(this::visitAnnotation);
    namedValueP.type().ifPresent(this::visitType);
    namedValueP.body().ifPresent(this::visitExpr);
    visitNameOf(namedValueP);
  }

  public void visitNamedFunc(NamedFuncP namedFuncP) {
    namedFuncP.annotation().ifPresent(this::visitAnnotation);
    namedFuncP.resT().ifPresent(this::visitType);
    visitParams(namedFuncP.params());
    namedFuncP.body().ifPresent(expr -> visitFuncBody(namedFuncP, expr));
    visitNameOf(namedFuncP);
  }

  public void visitFuncBody(FuncP funcP, ExprP expr) {
    visitExpr(expr);
  }

  public void visitAnnotation(AnnotationP annotation) {
    visitString(annotation.path());
  }

  public void visitParams(List<ItemP> params) {
    visitIndexedElements(params, this::visitParam);
  }

  public void visitParam(int index, ItemP param) {
    visitType(param.type());
    param.defaultValue().ifPresent(this::visitNamedValue);
    visitNameOf(param);
  }

  public void visitType(TypeP type) {}

  public void visitExpr(ExprP expr) {
    // @formatter:off
    switch (expr) {
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

  public void visitBlob(BlobP blob) {
  }

  public void visitCall(CallP call) {
    visitExpr(call.callee());
    visitArgs(call.args());
  }

  public void visitInt(IntP int_) {
  }

  public void visitAnonymousFunc(AnonymousFuncP anonymousFuncP) {
    visitParams(anonymousFuncP.params());
    visitFuncBody(anonymousFuncP, anonymousFuncP.bodyGet());
  }

  public void visitNamedArg(NamedArgP namedArg) {
    visitExpr(namedArg.expr());
  }

  public void visitOrder(OrderP order) {
    order.elems().forEach(this::visitExpr);
  }

  public void visitSelect(SelectP select) {
    visitExpr(select.selectable());
  }

  public void visitRef(RefP ref) {}

  public void visitString(StringP string) {}

  public void visitNameOf(RefableP refable) {}

  public <E> void visitIndexedElements(List<E> elems, BiConsumer<Integer, ? super E> consumer) {
    for (int i = 0; i < elems.size(); i++) {
      consumer.accept(i, elems.get(i));
    }
  }
}
