package org.smoothbuild.lang.parse.ast;

import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.function.BiConsumer;

import org.smoothbuild.lang.parse.ast.StructNode.ConstructorNode;

public class AstVisitor {
  public void visitAst(Ast ast) {
    visitStructs(ast.structs());
    visitEvaluables(ast.evaluables());
    visitEvaluables(map(ast.structs(), StructNode::constructor));
  }

  public void visitStructs(List<StructNode> structs) {
    structs.forEach(this::visitStruct);
  }

  public void visitStruct(StructNode struct) {
    visitFields(struct.fields());
  }

  public void visitConstructor(ConstructorNode constructor) {
    visitCallable(constructor);
  }

  public void visitCallable(CallableNode callable) {
    visitParams(callable.params());
  }

  public void visitFields(List<ItemNode> fields) {
    visitIndexedElements(fields, this::visitField);
  }

  public void visitField(int index, ItemNode field) {
    visitType(field.typeNode());
  }

  public void visitEvaluables(List<EvaluableNode> evaluable) {
    evaluable.forEach(this::visitEvaluable);
  }

  public void visitEvaluable(EvaluableNode evaluable) {
    if (evaluable instanceof FuncNode func) {
      visitFunc(func);
    } else if (evaluable instanceof ValueNode value) {
      visitValue(value);
    } else if (evaluable instanceof ConstructorNode constructor) {
      visitConstructor(constructor);
    } else {
      throw new RuntimeException(
          "Didn't expect instance of " + evaluable.getClass().getCanonicalName());
    }
  }

  public void visitValue(ValueNode value) {
    value.visitType(this);
    value.visitExpr(this);
  }

  public void visitFunc(FuncNode func) {
    func.visitType(this);
    func.visitExpr(this);
    visitCallable(func);
  }

  public void visitParams(List<ItemNode> params) {
    visitIndexedElements(params, this::visitParam);
  }

  public void visitParam(int index, ItemNode param) {
    visitType(param.typeNode());
    if (param.declaresDefaultValue()) {
      visitExpr(param.defaultValue());
    }
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

  public void visitBlobLiteral(BlobNode string) {
  }

  public <E> void visitIndexedElements(List<E> elements, BiConsumer<Integer, ? super E> consumer) {
    for (int i = 0; i < elements.size(); i++) {
      consumer.accept(i, elements.get(i));
    }
  }
}
