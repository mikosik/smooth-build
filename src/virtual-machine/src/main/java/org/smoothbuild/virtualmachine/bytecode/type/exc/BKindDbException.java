package org.smoothbuild.virtualmachine.bytecode.type.exc;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;

public class BKindDbException extends BytecodeException {
  public BKindDbException(String message) {
    super(message);
  }

  public BKindDbException(String message, Throwable cause) {
    super(message, cause);
  }

  public BKindDbException(Throwable cause) {
    super(cause);
  }
}
