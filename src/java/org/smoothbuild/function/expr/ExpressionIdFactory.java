package org.smoothbuild.function.expr;

public class ExpressionIdFactory {
  private int count = 0;

  public ExpressionId createId(String name) {
    // TODO hash value should be based on expression. In case of
    // FunctionExpression it should take Function.hash + hashes of all
    // Expression used as values for function arguments. In case of
    // StringExpression it should be based on string value.

    return new ExpressionId(Integer.toString(count++) + name);
  }
}
