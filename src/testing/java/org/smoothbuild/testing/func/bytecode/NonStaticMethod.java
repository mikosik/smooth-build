package org.smoothbuild.testing.func.bytecode;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;

public class NonStaticMethod {
  public ObjB bytecode(BytecodeF bytecodeF) {
    return bytecodeF.string("abc");
  }
}
