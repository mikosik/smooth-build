package org.smoothbuild.db.bytecode.type.exc;

import org.smoothbuild.db.bytecode.ByteCodeExc;

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
