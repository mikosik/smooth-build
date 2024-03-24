package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;

public class BExprDbException extends BytecodeException {
  public BExprDbException(String message) {
    super(message);
  }

  public BExprDbException(String message, Throwable cause) {
    super(message, cause);
  }

  public BExprDbException(Throwable cause) {
    super(cause);
  }
}
