package org.smoothbuild.registry.instantiate;

import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.Type;
import org.smoothbuild.lang.function.exc.FunctionException;

public class Expression {
  private final ExpressionId id;
  private final Type type;
  private final FunctionDefinition functionDefinition;

  public Expression(ExpressionId id, Type type, FunctionDefinition functionDefinition) {
    this.id = id;
    this.type = type;
    this.functionDefinition = functionDefinition;
  }

  public ExpressionId id() {
    return id;
  }

  public Type type() {
    return type;
  }

  public void execute() throws FunctionException {
    // TODO set param values from dependencies that should be passed to
    // constructor
    functionDefinition.execute();
  }
}
