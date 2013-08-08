package org.smoothbuild.registry.instantiate;

import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.exc.FunctionException;

public class FunctionInstance {
  private final FunctionInstanceId id;
  private final FunctionDefinition functionDefinition;

  public FunctionInstance(FunctionInstanceId id, FunctionDefinition functionDefinition) {
    this.id = id;
    this.functionDefinition = functionDefinition;
  }

  public FunctionInstanceId id() {
    return id;
  }

  public void execute() throws FunctionException {
    // TODO set param values from dependencies that should be passed to
    // constructor
    functionDefinition.execute();
  }
}
