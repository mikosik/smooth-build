package org.smoothbuild.lang.parse.ast;

import java.util.List;
import java.util.function.BiConsumer;

public class AstVisitor {
  public void visitAst(Ast ast) {
    visitStructs(ast.structs());
    visitEvaluable(ast.evaluables());
  }

  public void visitStructs(List<StructNode> structs) {
    structs.forEach(this::visitStruct);
  }

  public void visitStruct(StructNode struct) {
    visitFields(struct.fields());
  }

  public void visitFields(List<ItemNode> fields) {
    fields.forEach(this::visitField);
  }

  public void visitField(ItemNode field) {
    field.typeNode().ifPresent(this::visitType);
  }

  public void visitEvaluable(List<EvaluableNode> evaluable) {
    evaluable.forEach(this::visitEvaluable);
  }

  public void visitEvaluable(EvaluableNode evaluable) {
    switch (evaluable) {
      case RealFuncNode func -> visitRealFunc(func);
      case ValueNode value -> visitValue(value);
      default -> throw new RuntimeException(
          "Didn't expect instance of " + evaluable.getClass().getCanonicalName());
    }
  }

  public void visitValue(ValueNode value) {
    value.annotation().ifPresent(this::visitNative);
    value.typeNode().ifPresent(this::visitType);
    value.body().ifPresent(this::visitExpr);
  }

  public void visitRealFunc(RealFuncNode func) {
    func.annotation().ifPresent(this::visitNative);
    func.typeNode().ifPresent(this::visitType);
    visitParams(func.params());
    func.body().ifPresent(this::visitExpr);
  }

  public void visitNative(AnnotationNode annotation) {
    visitStringLiteral(annotation.path());
  }

  public void visitParams(List<ItemNode> params) {
    visitIndexedElements(params, this::visitParam);
  }

  public void visitParam(int index, ItemNode param) {
    param.typeNode().ifPresent(this::visitType);
    param.body().ifPresent(this::visitExpr);
  }

  public void visitType(TypeNode type) {}

  public void visitExpr(ExprNode expr) {
    switch (expr) {
      case ArrayNode arrayNode -> visitArray(arrayNode);
      case BlobNode blobNode -> visitBlobLiteral(blobNode);
      case CallNode callNode -> visitCall(callNode);
      case SelectNode selectNode -> visitSelect(selectNode);
      case IntNode intNode -> visitIntLiteral(intNode);
      case RefNode refNode -> visitRef(refNode);
      case StringNode stringNode -> visitStringLiteral(stringNode);
      case null, default -> throw new RuntimeException(
          "Unknown node " + expr.getClass().getSimpleName());
    }
  }

  public void visitArray(ArrayNode array) {
    array.elems().forEach(this::visitExpr);
  }

  public void visitBlobLiteral(BlobNode blob) {
  }

  public void visitCall(CallNode call) {
    visitExpr(call.function());
    visitArgs(call.args());
  }

  public void visitArgs(List<ArgNode> args) {
    args.forEach(this::visitArg);
  }

  public void visitArg(ArgNode arg) {
    visitExpr(arg.expr());
  }

  public void visitSelect(SelectNode expr) {
    visitExpr(expr.expr());
  }

  public void visitIntLiteral(IntNode int_) {
  }

  public void visitRef(RefNode ref) {}

  public void visitStringLiteral(StringNode string) {
  }

  public <E> void visitIndexedElements(List<E> elems, BiConsumer<Integer, ? super E> consumer) {
    for (int i = 0; i < elems.size(); i++) {
      consumer.accept(i, elems.get(i));
    }
  }
}
