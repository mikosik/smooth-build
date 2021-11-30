package org.smoothbuild.lang.parse.ast;

import java.util.List;
import java.util.function.BiConsumer;

public class AstVisitor {
  public void visitAst(Ast ast) {
    visitStructs(ast.structs());
    visitEvaluable(ast.evaluables());
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
    field.typeNode().ifPresent(this::visitType);
  }

  public void visitEvaluable(List<EvalN> evals) {
    evals.forEach(this::visitEvaluable);
  }

  public void visitEvaluable(EvalN eval) {
    switch (eval) {
      case FuncN func -> visitFunc(func);
      case ValN value -> visitValue(value);
      default -> throw new RuntimeException(
          "Didn't expect instance of " + eval.getClass().getCanonicalName());
    }
  }

  public void visitValue(ValN valN) {
    valN.ann().ifPresent(this::visitNative);
    valN.typeNode().ifPresent(this::visitType);
    valN.body().ifPresent(this::visitExpr);
  }

  public void visitFunc(FuncN funcN) {
    funcN.ann().ifPresent(this::visitNative);
    funcN.typeNode().ifPresent(this::visitType);
    visitParams(funcN.params());
    funcN.body().ifPresent(this::visitExpr);
  }

  public void visitNative(AnnN annotation) {
    visitStringLiteral(annotation.path());
  }

  public void visitParams(List<ItemN> params) {
    visitIndexedElements(params, this::visitParam);
  }

  public void visitParam(int index, ItemN param) {
    param.typeNode().ifPresent(this::visitType);
    param.body().ifPresent(this::visitExpr);
  }

  public void visitType(TypeN type) {}

  public void visitExpr(ExprN expr) {
    switch (expr) {
      case ArrayN arrayN -> visitArray(arrayN);
      case BlobN blobN -> visitBlobLiteral(blobN);
      case CallN callN -> visitCall(callN);
      case SelectN selectN -> visitSelect(selectN);
      case IntN intN -> visitIntLiteral(intN);
      case RefN refN -> visitRef(refN);
      case StringN stringN -> visitStringLiteral(stringN);
      case null, default -> throw new RuntimeException(
          "Unknown node " + expr.getClass().getSimpleName());
    }
  }

  public void visitArray(ArrayN array) {
    array.elems().forEach(this::visitExpr);
  }

  public void visitBlobLiteral(BlobN blob) {
  }

  public void visitCall(CallN call) {
    visitExpr(call.func());
    visitArgs(call.args());
  }

  public void visitArgs(List<ArgNode> args) {
    args.forEach(this::visitArg);
  }

  public void visitArg(ArgNode arg) {
    visitExpr(arg.expr());
  }

  public void visitSelect(SelectN expr) {
    visitExpr(expr.expr());
  }

  public void visitIntLiteral(IntN int_) {
  }

  public void visitRef(RefN ref) {}

  public void visitStringLiteral(StringN string) {
  }

  public <E> void visitIndexedElements(List<E> elems, BiConsumer<Integer, ? super E> consumer) {
    for (int i = 0; i < elems.size(); i++) {
      consumer.accept(i, elems.get(i));
    }
  }
}
