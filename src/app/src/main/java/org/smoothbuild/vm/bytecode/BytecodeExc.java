package org.smoothbuild.vm.bytecode;

public class BytecodeExc extends RuntimeException {
  public BytecodeExc(String message) {
    super(message);
  }

  public BytecodeExc(String message, Throwable cause) {
    super(message, cause);
  }

  public BytecodeExc(Throwable cause) {
    super(cause);
  }
}
