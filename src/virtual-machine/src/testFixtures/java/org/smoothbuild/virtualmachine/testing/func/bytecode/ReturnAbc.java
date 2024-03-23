package org.smoothbuild.virtualmachine.testing.func.bytecode;

import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

public class ReturnAbc {
  public static BValue bytecode(BytecodeFactory bytecodeFactory, Map<String, BType> varMap)
      throws BytecodeException {
    return bytecodeFactory.string("abc");
  }
}
