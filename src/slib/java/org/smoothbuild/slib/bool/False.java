package org.smoothbuild.slib.bool;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.type.cnst.TypeB;

public class False {
  public static ObjB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    return f.bool(false);
  }
}
