package org.smoothbuild.slib.core;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.val.InstB;
import org.smoothbuild.bytecode.type.val.TypeB;

public class IfFunc {
  public static InstB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    var t = varMap.get("A");
    return f.ifFunc(t);
  }
}
