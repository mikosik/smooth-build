package org.smoothbuild.expression;

import org.smoothbuild.function.Type;
import org.smoothbuild.plugin.exc.FunctionException;

public class InvalidExpression implements Expression {
  private final Type type;
  private final ExpressionId id;

  public InvalidExpression(Type type) {
    this.id = new ExpressionId("null");
    this.type = type;
  }

  @Override
  public ExpressionId id() {
    return id;
  }

  @Override
  public Type type() {
    return type;
  }

  @Override
  public void calculate() throws FunctionException {
    throw new RuntimeException("BrokenExpression cannot be calculated.");
  }

  @Override
  public Object result() {
    throw new RuntimeException("BrokenExpression cannot return result.");
  }
}
