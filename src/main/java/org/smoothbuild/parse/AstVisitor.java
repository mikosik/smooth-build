package org.smoothbuild.parse;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.ArrayNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.ExprNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.parse.ast.RefNode;
import org.smoothbuild.parse.ast.StringNode;
import org.smoothbuild.parse.ast.TypeNode;

public class AstVisitor {
  public void visitAst(Ast ast) {
    visitFuncs(ast.funcs());
  }

  public void visitFuncs(List<FuncNode> funcs) {
    visitElements(funcs, this::visitFunc);
  }

  public void visitFunc(FuncNode func) {
    visitType(func.type());
    visitParams(func.params());
    if (!func.isNative()) {
      visitExpr(func.expr());
    }
  }

  public void visitParams(List<ParamNode> params) {
    visitElements(params, this::visitParam);
  }

  public void visitParam(ParamNode param) {
    visitType(param.type());
    if (param.hasDefaultValue()) {
      visitExpr(param.defaultValue());
    }
  }

  public void visitType(TypeNode type) {}

  public void visitExpr(ExprNode expr) {
    if (expr instanceof ArrayNode) {
      visitArray((ArrayNode) expr);
    } else if (expr instanceof CallNode) {
      visitCall((CallNode) expr);
    } else if (expr instanceof RefNode) {
      visitRef((RefNode) expr);
    } else if (expr instanceof StringNode) {
      visitString((StringNode) expr);
    } else {
      throw new RuntimeException("Unknown node " + expr.getClass().getSimpleName());
    }
  }

  public void visitArray(ArrayNode array) {
    visitElements(array.elements(), this::visitExpr);
  }

  public void visitCall(CallNode call) {
    visitArgs(call.args());
  }

  public void visitRef(RefNode ref) {}

  public void visitArgs(List<ArgNode> args) {
    visitElements(args, this::visitArg);
  }

  public void visitArg(ArgNode arg) {
    visitExpr(arg.expr());
  }

  public void visitString(StringNode string) {}

  public <E> void visitElements(List<E> elements, Consumer<? super E> consumer) {
    elements
        .stream()
        .forEach(consumer);
  }
}
