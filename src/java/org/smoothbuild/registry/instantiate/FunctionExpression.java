package org.smoothbuild.registry.instantiate;

import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.Type;
import org.smoothbuild.lang.function.exc.FunctionException;

public class FunctionExpression implements Expression {
  private final ExpressionId id;
  private final Type type;
  private final FunctionDefinition functionDefinition;

  public FunctionExpression(ExpressionId id, Type type, FunctionDefinition functionDefinition) {
    this.id = id;
    this.type = type;
    this.functionDefinition = functionDefinition;
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
  public void execute() throws FunctionException {
    // TODO set param values from dependencies that should be passed to
    // constructor
    functionDefinition.execute();
  }

  @Override
  public Object result() {
    return null;
  }
}
