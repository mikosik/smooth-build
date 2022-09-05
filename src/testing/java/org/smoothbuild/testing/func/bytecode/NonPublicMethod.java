package org.smoothbuild.testing.func.bytecode;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.bytecode.type.val.TypeB;

public class NonPublicMethod {
  static ValB bytecode(BytecodeF bytecodeF, Map<String, TypeB> varMap) {
    return bytecodeF.string("abc");
  }
}
