package org.smoothbuild.lang.function;

public interface Function {
  public Params params();

  public void execute() throws FunctionException;

  public Object result();
}
