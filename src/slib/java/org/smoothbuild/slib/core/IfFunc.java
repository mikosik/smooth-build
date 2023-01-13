package org.smoothbuild.slib.core;

import java.util.Map;

import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

/**
 * A if(Bool condition, A then, A else)
 */
public class IfFunc {
  public static ValueB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    var t = varMap.get("A");
    return f.ifFunc(t);
  }
}
