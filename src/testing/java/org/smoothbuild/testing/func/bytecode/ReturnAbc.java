package org.smoothbuild.testing.func.bytecode;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.type.inst.TypeB;

public class ReturnAbc {
  public static InstB bytecode(BytecodeF bytecodeF, Map<String, TypeB> varMap) {
    return bytecodeF.string("abc");
  }
}
