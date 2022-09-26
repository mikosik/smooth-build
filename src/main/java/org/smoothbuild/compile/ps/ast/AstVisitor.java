package org.smoothbuild.compile.ps.ast;

import java.util.List;
import java.util.function.BiConsumer;

import org.smoothbuild.compile.ps.ast.expr.BlobP;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.DefaultArgP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.IntP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.OrderP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.SelectP;
import org.smoothbuild.compile.ps.ast.expr.StringP;
import org.smoothbuild.compile.ps.ast.refable.FuncP;
import org.smoothbuild.compile.ps.ast.refable.ItemP;
import org.smoothbuild.compile.ps.ast.refable.NamedValP;
import org.smoothbuild.compile.ps.ast.refable.PolyEvaluableP;
import org.smoothbuild.compile.ps.ast.type.TypeP;

public class AstVisitor {
  public void visitAst(Ast ast) {
    visitStructs(ast.structs());
    visitEvaluable(ast.evaluables());
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
  }

  public void visitEvaluable(List<PolyEvaluableP> evaluables) {
    evaluables.forEach(this::visitEvaluable);
  }

  public void visitEvaluable(PolyEvaluableP evaluable) {
    switch (evaluable) {
      case FuncP func -> visitFunc(func);
      case NamedValP value -> visitValue(value);
    }
  }

  public void visitValue(NamedValP namedValP) {
    namedValP.ann().ifPresent(this::visitAnn);
    namedValP.type().ifPresent(this::visitType);
    namedValP.body().ifPresent(this::visitExpr);
  }

  public void visitFunc(FuncP funcP) {
    funcP.ann().ifPresent(this::visitAnn);
    funcP.resT().ifPresent(this::visitType);
    visitParams(funcP.params());
    funcP.body().ifPresent(this::visitExpr);
  }

  public void visitAnn(AnnP annotation) {
    visitString(annotation.path());
  }

  public void visitParams(List<ItemP> params) {
    visitIndexedElements(params, this::visitParam);
  }

  public void visitParam(int index, ItemP param) {
    visitType(param.type());
    param.body().ifPresent(this::visitExpr);
  }

  public void visitType(TypeP type) {}

  public void visitExpr(ExprP expr) {
    switch (expr) {
      case OrderP orderP -> visitOrder(orderP);
      case BlobP blobP -> visitBlob(blobP);
      case CallP callP -> visitCall(callP);
      case DefaultArgP defaultArgP -> visitDefaultArg(defaultArgP);
      case IntP intP -> visitInt(intP);
      case NamedArgP namedArgP -> visitNamedArg(namedArgP);
      case RefP refP -> visitRef(refP);
      case SelectP selectP -> visitSelect(selectP);
      case StringP stringP -> visitString(stringP);
    }
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

  public void visitDefaultArg(DefaultArgP defaultArg) {
  }

  public void visitInt(IntP int_) {
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

  public void visitString(StringP string) {
  }

  public <E> void visitIndexedElements(List<E> elems, BiConsumer<Integer, ? super E> consumer) {
    for (int i = 0; i < elems.size(); i++) {
      consumer.accept(i, elems.get(i));
    }
  }
}
