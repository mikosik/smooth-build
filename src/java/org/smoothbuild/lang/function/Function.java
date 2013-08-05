package org.smoothbuild.lang.function;

import org.smoothbuild.lang.function.exc.FunctionException;

public interface Function {
  public Params params();

  public void execute() throws FunctionException;

  public Object result();
}
