package org.smoothbuild.virtualmachine.testing.func.bytecode;

import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

public class ThrowException {
  public static BValue bytecode(BytecodeFactory bytecodeFactory, Map<String, BType> varMap) {
    throw new UnsupportedOperationException("detailed message");
  }
}
