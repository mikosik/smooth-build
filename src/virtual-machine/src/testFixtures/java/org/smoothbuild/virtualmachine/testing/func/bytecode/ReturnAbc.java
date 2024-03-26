package org.smoothbuild.virtualmachine.testing.func.bytecode;

import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

public class ReturnAbc {
  public static BValue bytecode(BytecodeFactory bytecodeFactory, Map<String, BType> varMap)
      throws BytecodeException {
    return bytecodeFactory.string("abc");
  }
}
