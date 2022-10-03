package org.smoothbuild.slib.core;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.val.InstB;
import org.smoothbuild.bytecode.type.val.TypeB;

/**
 * [R] map([S] array, R(S) mapper)
 */
public class MapFunc {
  public static InstB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    var r = varMap.get("R");
    var s = varMap.get("S");
    return f.mapFunc(r, s);
  }
}
