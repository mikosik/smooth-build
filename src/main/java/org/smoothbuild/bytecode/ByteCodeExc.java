package org.smoothbuild.bytecode;

public class ByteCodeExc extends RuntimeException {
  public ByteCodeExc(String message) {
    super(message);
  }

  public ByteCodeExc(String message, Throwable cause) {
    super(message, cause);
  }

  public ByteCodeExc(Throwable cause) {
    super(cause);
  }
}
