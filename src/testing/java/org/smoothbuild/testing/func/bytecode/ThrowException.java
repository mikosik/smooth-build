package org.smoothbuild.testing.func.bytecode;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.type.inst.TypeB;

public class ThrowException {
  public static InstB bytecode(BytecodeF bytecodeF, Map<String, TypeB> varMap) {
    throw new UnsupportedOperationException("detailed message");
  }
}
