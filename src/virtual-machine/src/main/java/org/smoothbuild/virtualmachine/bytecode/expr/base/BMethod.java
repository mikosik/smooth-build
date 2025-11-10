package org.smoothbuild.virtualmachine.bytecode.expr.base;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;

public class BMethod {
  private static final int JAR_INDEX = 0;
  private static final int CLASS_BINARY_NAME_INDEX = 1;
  private static final int METHOD_NAME_INDEX = 2;

  private final BTuple method;

  public BMethod(BTuple method) {
    this.method = method;
  }

  public BBlob jar() throws BytecodeException {
    return (BBlob) method.elements().get(JAR_INDEX);
  }

  public BString classBinaryName() throws BytecodeException {
    return (BString) method.elements().get(CLASS_BINARY_NAME_INDEX);
  }

  public BString methodName() throws BytecodeException {
    return (BString) method.elements().get(METHOD_NAME_INDEX);
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
