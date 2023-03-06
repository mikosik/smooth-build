package org.smoothbuild.vm.bytecode.type.exc;

import org.smoothbuild.vm.bytecode.BytecodeExc;

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
