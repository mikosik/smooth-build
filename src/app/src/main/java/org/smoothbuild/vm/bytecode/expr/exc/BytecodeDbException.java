package org.smoothbuild.vm.bytecode.expr.exc;

import org.smoothbuild.vm.bytecode.BytecodeException;

public class BytecodeDbException extends BytecodeException {
  public BytecodeDbException(String message) {
    super(message);
  }

  public BytecodeDbException(String message, Throwable cause) {
    super(message, cause);
  }

  public BytecodeDbException(Throwable cause) {
    super(cause);
  }
}
