package org.smoothbuild.testing.nativefunc;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;

public class NonStaticBytecodeMethod {
  public ObjB bytecode(BytecodeF bytecodeF) {
    return bytecodeF.string("abc");
  }
}
