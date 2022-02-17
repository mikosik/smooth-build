package org.smoothbuild.slib.bool;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;

public class True {
  public static ObjB bytecode(BytecodeF f) {
    return f.bool(true);
  }
}
