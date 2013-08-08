package org.smoothbuild.registry.instantiate;

import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.exc.FunctionException;

public class Expression {
  private final ExpressionId id;
  private final FunctionDefinition functionDefinition;

  public Expression(ExpressionId id, FunctionDefinition functionDefinition) {
    this.id = id;
    this.functionDefinition = functionDefinition;
  }

  public ExpressionId id() {
    return id;
  }

  public void execute() throws FunctionException {
    // TODO set param values from dependencies that should be passed to
    // constructor
    functionDefinition.execute();
  }
}
