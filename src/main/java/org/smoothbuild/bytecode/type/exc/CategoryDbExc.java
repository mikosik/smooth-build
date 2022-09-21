package org.smoothbuild.bytecode.type.exc;

import org.smoothbuild.bytecode.BytecodeExc;

public class CategoryDbExc extends BytecodeExc {
  public CategoryDbExc(String message) {
    super(message);
  }

  public CategoryDbExc(String message, Throwable cause) {
    super(message, cause);
  }

  public CategoryDbExc(Throwable cause) {
    super(cause);
  }
}
