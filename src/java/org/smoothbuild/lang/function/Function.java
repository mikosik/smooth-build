package org.smoothbuild.lang.function;

import org.smoothbuild.lang.function.exc.FunctionException;

public interface Function {
  public Params params();

  public Object execute() throws FunctionException;
}
