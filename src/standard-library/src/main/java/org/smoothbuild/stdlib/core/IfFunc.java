package org.smoothbuild.stdlib.core;

import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

/**
 * A if(Bool condition, A then, A else)
 */
public class IfFunc {
  public static BValue bytecode(BytecodeFactory f, Map<String, BType> varMap)
      throws BytecodeException {
    var t = varMap.get("A");
    return f.ifFunc(t);
  }
}
