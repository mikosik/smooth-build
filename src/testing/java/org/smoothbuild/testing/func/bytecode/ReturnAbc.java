package org.smoothbuild.testing.func.bytecode;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.type.cnst.TypeB;

public class ReturnAbc {
  public static CnstB bytecode(BytecodeF bytecodeF, Map<String, TypeB> varMap) {
    return bytecodeF.string("abc");
  }
}
