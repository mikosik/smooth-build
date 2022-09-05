package org.smoothbuild.slib.bool;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.type.cnst.TypeB;

public class True {
  public static CnstB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    return f.bool(true);
  }
}
