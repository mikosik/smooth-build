package org.smoothbuild.stdlib.core;

import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

/**
 * [R] map([S] array, S->R mapper)
 */
public class MapFunc {
  public static ValueB bytecode(BytecodeFactory f, Map<String, TypeB> varMap)
      throws BytecodeException {
    var r = varMap.get("R");
    var s = varMap.get("S");
    return f.mapFunc(r, s);
  }
}
