package org.smoothbuild.slib.bool;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.bytecode.type.value.TypeB;

public class True {
  public static ValueB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    return f.bool(true);
  }
}
