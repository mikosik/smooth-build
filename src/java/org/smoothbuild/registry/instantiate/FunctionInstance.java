package org.smoothbuild.registry.instantiate;

import org.smoothbuild.lang.function.Function;
import org.smoothbuild.lang.function.exc.FunctionException;

public class FunctionInstance {
  private final FunctionInstanceId id;
  private final Function function;

  public FunctionInstance(FunctionInstanceId id, Function function) {
    this.id = id;
    this.function = function;
  }

  public FunctionInstanceId id() {
    return id;
  }

  public void execute() throws FunctionException {
    // TODO set param values from dependencies that should be passed to
    // constructor
    function.execute();
  }
}
