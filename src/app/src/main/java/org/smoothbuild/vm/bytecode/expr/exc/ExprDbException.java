package org.smoothbuild.vm.bytecode.expr.exc;

import org.smoothbuild.vm.bytecode.BytecodeException;

public class ExprDbException extends BytecodeException {
  public ExprDbException(String message) {
    super(message);
  }

  public ExprDbException(String message, Throwable cause) {
    super(message, cause);
  }

  public ExprDbException(Throwable cause) {
    super(cause);
  }
}
