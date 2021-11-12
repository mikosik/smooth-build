package org.smoothbuild.lang.parse.ast;

import java.util.List;
import java.util.function.BiConsumer;

public class AstVisitor {
  public void visitAst(Ast ast) {
    visitStructs(ast.structs());
    visitReferencable(ast.referencables());
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

  public void visitReferencable(List<ReferencableNode> referencable) {
    referencable.forEach(this::visitReferencable);
  }

  public void visitReferencable(ReferencableNode referencable) {
    if (referencable instanceof RealFuncNode func) {
      visitRealFunc(func);
    } else if (referencable instanceof ValueNode value) {
      visitValue(value);
    } else {
      throw new RuntimeException(
          "Didn't expect instance of " + referencable.getClass().getCanonicalName());
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
    if (expr instanceof ArrayNode arrayNode) {
      visitArray(arrayNode);
    } else if (expr instanceof BlobNode blobNode) {
      visitBlobLiteral(blobNode);
    } else if (expr instanceof CallNode callNode) {
      visitCall(callNode);
    } else if (expr instanceof SelectNode selectNode) {
      visitSelect(selectNode);
    } else if (expr instanceof IntNode intNode) {
      visitIntLiteral(intNode);
    } else if (expr instanceof RefNode refNode) {
      visitRef(refNode);
    } else if (expr instanceof StringNode stringNode) {
      visitStringLiteral(stringNode);
    } else {
      throw new RuntimeException("Unknown node " + expr.getClass().getSimpleName());
    }
  }

  public void visitArray(ArrayNode array) {
    array.elements().forEach(this::visitExpr);
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

  public <E> void visitIndexedElements(List<E> elements, BiConsumer<Integer, ? super E> consumer) {
    for (int i = 0; i < elements.size(); i++) {
      consumer.accept(i, elements.get(i));
    }
  }
}
