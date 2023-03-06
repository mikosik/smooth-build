package org.smoothbuild.vm.bytecode.expr.exc;

import org.smoothbuild.vm.bytecode.BytecodeExc;

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
