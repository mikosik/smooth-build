package org.smoothbuild.lang.function;

import org.smoothbuild.lang.function.exc.FunctionException;

public interface FunctionDefinition {
  public Params params();

  public Object execute() throws FunctionException;
}
