package org.smoothbuild.virtualmachine.bytecode.expr.base;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;

public class BMethod {
  private final BTuple method;

  public BMethod(BTuple method) {
    this.method = method;
  }

  public BBlob jar() throws BytecodeException {
    return (BBlob) method.elements().get(0);
  }

  public BString classBinaryName() throws BytecodeException {
    return (BString) method.elements().get(1);
  }

  public BString methodName() throws BytecodeException {
    return (BString) method.elements().get(2);
  }

  public BTuple tuple() {
    return method;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof BMethod that && this.method.equals(that.method);
  }

  @Override
  public int hashCode() {
    return method.hashCode();
  }
}
