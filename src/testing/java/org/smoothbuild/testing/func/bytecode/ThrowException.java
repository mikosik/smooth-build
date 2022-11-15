package org.smoothbuild.testing.func.bytecode;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.inst.ValueB;
import org.smoothbuild.bytecode.type.inst.TypeB;

public class ThrowException {
  public static ValueB bytecode(BytecodeF bytecodeF, Map<String, TypeB> varMap) {
    throw new UnsupportedOperationException("detailed message");
  }
}
