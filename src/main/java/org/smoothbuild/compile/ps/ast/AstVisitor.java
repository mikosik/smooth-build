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
import org.smoothbuild.compile.ps.ast.refable.EvaluableP;
import org.smoothbuild.compile.ps.ast.refable.ItemP;
import org.smoothbuild.compile.ps.ast.refable.NamedFuncP;
import org.smoothbuild.compile.ps.ast.refable.NamedValueP;
import org.smoothbuild.compile.ps.ast.refable.RefableP;
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
    visitIdentifier(field);
  }

  public void visitEvaluable(List<EvaluableP> evaluables) {
    evaluables.forEach(this::visitEvaluable);
  }

  public void visitEvaluable(EvaluableP evaluable) {
    switch (evaluable) {
      case NamedFuncP func -> visitNamedFunc(func);
      case NamedValueP value -> visitNamedValue(value);
    }
  }

  public void visitNamedValue(NamedValueP namedValueP) {
    namedValueP.ann().ifPresent(this::visitAnn);
    namedValueP.type().ifPresent(this::visitType);
    namedValueP.body().ifPresent(this::visitExpr);
    visitIdentifier(namedValueP);
  }

  public void visitNamedFunc(NamedFuncP namedFuncP) {
    namedFuncP.ann().ifPresent(this::visitAnn);
    namedFuncP.resT().ifPresent(this::visitType);
    visitParams(namedFuncP.params());
    namedFuncP.body().ifPresent(this::visitExpr);
    visitIdentifier(namedFuncP);
  }

  public void visitAnn(AnnP annotation) {
    visitString(annotation.path());
  }

  public void visitParams(List<ItemP> params) {
    visitIndexedElements(params, this::visitParam);
  }

  public void visitParam(int index, ItemP param) {
    visitType(param.type());
    param.defaultVal().ifPresent(this::visitExpr);
    visitIdentifier(param);
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

  public void visitString(StringP string) {}

  public void visitIdentifier(RefableP refable) {}

  public <E> void visitIndexedElements(List<E> elems, BiConsumer<Integer, ? super E> consumer) {
    for (int i = 0; i < elems.size(); i++) {
      consumer.accept(i, elems.get(i));
    }
  }
}
