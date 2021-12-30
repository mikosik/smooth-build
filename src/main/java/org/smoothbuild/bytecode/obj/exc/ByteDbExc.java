package org.smoothbuild.bytecode.obj.exc;

import org.smoothbuild.bytecode.ByteCodeExc;

public class ByteDbExc extends ByteCodeExc {
  public ByteDbExc(String message) {
    super(message);
  }

  public ByteDbExc(String message, Throwable cause) {
    super(message, cause);
  }

  public ByteDbExc(Throwable cause) {
    super(cause);
  }
}
