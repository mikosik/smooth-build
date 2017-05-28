package org.smoothbuild.parse.ast;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.reducing;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.Type;

public abstract class AstWalker<T> {
  public T visitAst(Ast ast) {
    return visitFunctions(ast.functions());
  }

  public T visitFunctions(List<FunctionNode> functions) {
    return visitElements(functions, this::visitFunction);
  }

  public T visitFunction(FunctionNode function) {
    return reduce(
        visitName(function.name()),
        visitParams(function.params()),
        visitExpr(function.expr()));
  }

  public T visitName(Name name) {
    return reduceIdentity();
  }

  public T visitParams(List<ParamNode> params) {
    return visitElements(params, this::visitParam);
  }

  public T visitParam(ParamNode param) {
    return visitType(param.type());
  }

  public T visitType(Type type) {
    return reduceIdentity();
  }

  public T visitExpr(ExprNode expr) {
    if (expr instanceof ArrayNode) {
      return visitArray((ArrayNode) expr);
    } else if (expr instanceof CallNode) {
      return visitCall((CallNode) expr);
    } else if (expr instanceof StringNode) {
      return visitString((StringNode) expr);
    } else {
      throw new RuntimeException("Unknown node " + expr.getClass().getSimpleName());
    }
  }

  public T visitArray(ArrayNode array) {
    return visitElements(array.elements(), this::visitExpr);
  }

  public T visitCall(CallNode call) {
    return visitArgs(call.args());
  }

  public T visitArgs(List<ArgNode> args) {
    return visitElements(args, this::visitArg);
  }

  public T visitArg(ArgNode arg) {
    return visitExpr(arg.expr());
  }

  public T visitString(StringNode expr) {
    return reduceIdentity();
  }

  public <E> T visitElements(List<E> elements,
      Function<? super E, ? extends T> mapper) {
    return elements
        .stream()
        .map(mapper)
        .collect(reducingCollector());
  }

  public T reduce(T... elements) {
    return asList(elements)
        .stream()
        .collect(reducingCollector());
  }

  public Collector<T, ?, T> reducingCollector() {
    return reducing(reduceIdentity(), this::reduce);
  }

  public abstract T reduce(T a, T b);

  public abstract T reduceIdentity();
}
