package org.smoothbuild.vm.bytecode;

public class BytecodeException extends RuntimeException {
  public BytecodeException(String message) {
    super(message);
  }

  public BytecodeException(String message, Throwable cause) {
    super(message, cause);
  }

  public BytecodeException(Throwable cause) {
    super(cause);
  }
}
