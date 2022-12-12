package org.smoothbuild.slib.core;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.bytecode.type.value.TypeB;

/**
 * [R] map([S] array, R(S) mapper)
 */
public class MapFunc {
  public static ValueB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    var r = varMap.get("R");
    var s = varMap.get("S");
    return f.mapFunc(r, s);
  }
}
