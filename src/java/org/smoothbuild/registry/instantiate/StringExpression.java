package org.smoothbuild.registry.instantiate;

import org.smoothbuild.lang.function.Type;
import org.smoothbuild.lang.function.exc.FunctionException;

public class StringExpression implements Expression {
  private final ExpressionId id;
  private final String string;

  public StringExpression(ExpressionId id, String string) {
    this.id = id;
    this.string = string;
  }

  @Override
  public ExpressionId id() {
    return id;
  }

  @Override
  public Type type() {
    return Type.STRING;
  }

  @Override
  public void calculate() throws FunctionException {}

  @Override
  public Object result() {
    return string;
  }
}
