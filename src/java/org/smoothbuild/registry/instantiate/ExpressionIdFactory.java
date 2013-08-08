package org.smoothbuild.registry.instantiate;


public class ExpressionIdFactory {
  private int count = 0;

  public ExpressionId createId(String name) {
    // TODO hash should be calculated from function name (in future version
    // together with package) and hashes of function that provide values for
    // parameters.

    return new ExpressionId(Integer.toString(count++) + name);
  }
}
