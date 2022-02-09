package org.smoothbuild.bytecode.type.exc;

import org.smoothbuild.bytecode.BytecodeExc;

public class CatDbExc extends BytecodeExc {
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
