package org.smoothbuild.testing.nativefunc;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;

public class ThrowExceptionBytecode {
  public static ObjB bytecode(BytecodeF bytecodeF) {
    throw new UnsupportedOperationException("detailed message");
  }
}
