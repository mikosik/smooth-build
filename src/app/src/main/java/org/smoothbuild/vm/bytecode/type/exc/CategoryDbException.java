package org.smoothbuild.vm.bytecode.type.exc;

import org.smoothbuild.vm.bytecode.BytecodeException;

public class CategoryDbException extends BytecodeException {
  public CategoryDbException(String message) {
    super(message);
  }

  public CategoryDbException(String message, Throwable cause) {
    super(message, cause);
  }

  public CategoryDbException(Throwable cause) {
    super(cause);
  }
}
