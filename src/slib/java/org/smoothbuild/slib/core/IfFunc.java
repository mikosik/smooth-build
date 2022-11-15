package org.smoothbuild.slib.core;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.inst.ValueB;
import org.smoothbuild.bytecode.type.inst.TypeB;

public class IfFunc {
  public static ValueB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    var t = varMap.get("A");
    return f.ifFunc(t);
  }
}
