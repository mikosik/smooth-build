package org.smoothbuild.registry.instantiate;


public class FunctionInstanceIdFactory {
  private int count = 0;

  public FunctionInstanceId createId(String name) {
    // TODO hash should be calculated from function name (in future version
    // together with package) and hashes of function that provide values for
    // parameters.

    return new FunctionInstanceId(Integer.toString(count++) + name);
  }
}
