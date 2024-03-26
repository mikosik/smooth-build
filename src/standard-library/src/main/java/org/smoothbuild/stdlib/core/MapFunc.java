package org.smoothbuild.stdlib.core;

import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

/**
 * [R] map([S] array, S->R mapper)
 */
public class MapFunc {
  public static BValue bytecode(BytecodeFactory f, Map<String, BType> varMap)
      throws BytecodeException {
    var r = varMap.get("R");
    var s = varMap.get("S");
    return f.mapFunc(r, s);
  }
}
