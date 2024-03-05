package org.smoothbuild.stdlib.core;

import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

/**
 * A if(Bool condition, A then, A else)
 */
public class IfFunc {
  public static ValueB bytecode(BytecodeFactory f, Map<String, TypeB> varMap)
      throws BytecodeException {
    var t = varMap.get("A");
    return f.ifFunc(t);
  }
}
