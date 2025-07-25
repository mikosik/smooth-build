package org.smoothbuild.stdlib.bool;

import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

public class True {
  public static BValue bytecode(BytecodeFactory f, Map<String, BType> varMap)
      throws BytecodeException {
    return f.bool(true);
  }
}
