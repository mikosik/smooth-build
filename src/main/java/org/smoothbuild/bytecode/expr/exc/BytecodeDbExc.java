package org.smoothbuild.bytecode.expr.exc;

import org.smoothbuild.bytecode.BytecodeExc;

public class BytecodeDbExc extends BytecodeExc {
  public BytecodeDbExc(String message) {
    super(message);
  }

  public BytecodeDbExc(String message, Throwable cause) {
    super(message, cause);
  }

  public BytecodeDbExc(Throwable cause) {
    super(cause);
  }
}
