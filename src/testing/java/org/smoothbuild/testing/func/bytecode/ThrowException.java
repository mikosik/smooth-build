package org.smoothbuild.testing.func.bytecode;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;

public class ThrowException {
  public static ObjB bytecode(BytecodeF bytecodeF) {
    throw new UnsupportedOperationException("detailed message");
  }
}
