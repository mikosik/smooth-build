package org.smoothbuild.testing.func.bytecode;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.bytecode.type.value.TypeB;

public class ReturnAbc {
  public static ValueB bytecode(BytecodeF bytecodeF, Map<String, TypeB> varMap) {
    return bytecodeF.string("abc");
  }
}
