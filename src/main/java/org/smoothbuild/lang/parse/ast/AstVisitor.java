package org.smoothbuild.lang.parse.ast;

import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.function.BiConsumer;

import org.smoothbuild.lang.parse.ast.StructNode.ConstructorNode;

public class AstVisitor {
  public void visitAst(Ast ast) {
    visitStructs(ast.structs());
    visitReferencable(map(ast.structs(), StructNode::constructor));
    visitReferencable(ast.referencable());
  }

  public void visitStructs(List<StructNode> structs) {
    structs.forEach(this::visitStruct);
  }

  public void visitStruct(StructNode struct) {
    visitFields(struct.fields());
  }

  public void visitConstructor(ConstructorNode constructor) {
    visitFunction(constructor);
  }

  public void visitFunction(FunctionNode function) {
    visitParams(function.params());
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
      visitFunc(func);
    } else if (referencable instanceof ValueNode value) {
      visitValue(value);
    } else if (referencable instanceof ConstructorNode constructor) {
      visitConstructor(constructor);
    } else {
      throw new RuntimeException(
          "Didn't expect instance of " + referencable.getClass().getCanonicalName());
    }
  }

  public void visitValue(ValueNode value) {
    value.nativ().ifPresent(this::visitNative);
    value.typeNode().ifPresent(this::visitType);
    value.body().ifPresent(this::visitExpr);
  }

  public void visitFunc(RealFuncNode func) {
    func.nativ().ifPresent(this::visitNative);
    func.typeNode().ifPresent(this::visitType);
    func.body().ifPresent(this::visitExpr);
    visitFunction(func);
  }

  public void visitNative(NativeNode nativ) {
    visitStringLiteral(nativ.path());
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
    if (expr instanceof FieldReadNode fieldReadNode) {
      visitFieldRead(fieldReadNode);
    } else if (expr instanceof ArrayNode arrayNode) {
      visitArray(arrayNode);
    } else if (expr instanceof CallNode callNode) {
      visitCall(callNode);
    } else if (expr instanceof RefNode refNode) {
      visitRef(refNode);
    } else if (expr instanceof StringNode stringNode) {
      visitStringLiteral(stringNode);
    } else if (expr instanceof BlobNode blobNode) {
      visitBlobLiteral(blobNode);
    } else if (expr instanceof IntNode intNode) {
      visitIntLiteral(intNode);
    } else {
      throw new RuntimeException("Unknown node " + expr.getClass().getSimpleName());
    }
  }

  public void visitFieldRead(FieldReadNode expr) {
    visitExpr(expr.expr());
  }

  public void visitArray(ArrayNode array) {
    array.elements().forEach(this::visitExpr);
  }

  public void visitCall(CallNode call) {
    visitExpr(call.function());
    visitArgs(call.args());
  }

  public void visitRef(RefNode ref) {}

  public void visitArgs(List<ArgNode> args) {
    args.forEach(this::visitArg);
  }

  public void visitArg(ArgNode arg) {
    visitExpr(arg.expr());
  }

  public void visitStringLiteral(StringNode string) {
  }

  public void visitBlobLiteral(BlobNode blob) {
  }

  public void visitIntLiteral(IntNode int_) {
  }

  public <E> void visitIndexedElements(List<E> elements, BiConsumer<Integer, ? super E> consumer) {
    for (int i = 0; i < elements.size(); i++) {
      consumer.accept(i, elements.get(i));
    }
  }
}
