package org.smoothbuild.bytecode.type.exc;

import org.smoothbuild.bytecode.ByteCodeExc;

public class CatDbExc extends ByteCodeExc {
  public CatDbExc(String message) {
    super(message);
  }

  public CatDbExc(String message, Throwable cause) {
    super(message, cause);
  }

  public CatDbExc(Throwable cause) {
    super(cause);
  }
}
