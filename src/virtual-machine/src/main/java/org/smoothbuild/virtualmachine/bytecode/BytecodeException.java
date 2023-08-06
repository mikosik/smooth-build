package org.smoothbuild.virtualmachine.bytecode;

import java.io.IOException;

public class BytecodeException extends Exception {
  public BytecodeException(String message) {
    super(message);
  }

  public BytecodeException(String message, Throwable cause) {
    super(message, cause);
  }

  public BytecodeException(Throwable cause) {
    super(cause);
  }

  public IOException toIOException() {
    return new IOException(this);
  }
}
