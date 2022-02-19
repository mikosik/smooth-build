package org.smoothbuild.parse.ast;

import static org.smoothbuild.util.Throwables.unexpectedCaseExc;

import java.util.List;
import java.util.function.BiConsumer;

public class AstVisitor {
  public void visitAst(Ast ast) {
    visitStructs(ast.structs());
    visitEvaluable(ast.topEvals());
  }

  public void visitStructs(List<StructN> structs) {
    structs.forEach(this::visitStruct);
  }

  public void visitStruct(StructN struct) {
    visitFields(struct.fields());
  }

  public void visitFields(List<ItemN> fields) {
    fields.forEach(this::visitField);
  }

  public void visitField(ItemN field) {
    field.evalT().ifPresent(this::visitType);
  }

  public void visitEvaluable(List<EvalN> evals) {
    evals.forEach(this::visitEvaluable);
  }

  public void visitEvaluable(EvalN eval) {
    switch (eval) {
      case FuncN func -> visitFunc(func);
      case ValN value -> visitValue(value);
      case ItemN item -> throw unexpectedCaseExc(item);
    }
  }

  public void visitValue(ValN valN) {
    valN.ann().ifPresent(this::visitAnn);
    valN.evalT().ifPresent(this::visitType);
    valN.body().ifPresent(this::visitExpr);
  }

  public void visitFunc(FuncN funcN) {
    funcN.ann().ifPresent(this::visitAnn);
    funcN.evalT().ifPresent(this::visitType);
    visitParams(funcN.params());
    funcN.body().ifPresent(this::visitExpr);
  }

  public void visitAnn(AnnN annotation) {
    visitString(annotation.path());
  }

  public void visitParams(List<ItemN> params) {
    visitIndexedElements(params, this::visitParam);
  }

  public void visitParam(int index, ItemN param) {
    param.evalT().ifPresent(this::visitType);
    param.body().ifPresent(this::visitExpr);
  }

  public void visitType(TypeN type) {}

  public void visitExpr(ExprN expr) {
    switch (expr) {
      case OrderN orderN -> visitOrder(orderN);
      case BlobN blobN -> visitBlob(blobN);
      case CallN callN -> visitCall(callN);
      case IntN intN -> visitInt(intN);
      case RefN refN -> visitRef(refN);
      case SelectN selectN -> visitSelect(selectN);
      case StringN stringN -> visitString(stringN);
    }
  }

  public void visitOrder(OrderN order) {
    order.elems().forEach(this::visitExpr);
  }

  public void visitBlob(BlobN blob) {
  }

  public void visitCall(CallN call) {
    visitExpr(call.callable());
    visitArgs(call.args());
  }

  public void visitArgs(List<ArgNode> args) {
    args.forEach(this::visitArg);
  }

  public void visitArg(ArgNode arg) {
    visitExpr(arg.expr());
  }

  public void visitSelect(SelectN select) {
    visitExpr(select.selectable());
  }

  public void visitInt(IntN int_) {
  }

  public void visitRef(RefN ref) {}

  public void visitString(StringN string) {
  }

  public <E> void visitIndexedElements(List<E> elems, BiConsumer<Integer, ? super E> consumer) {
    for (int i = 0; i < elems.size(); i++) {
      consumer.accept(i, elems.get(i));
    }
  }
}