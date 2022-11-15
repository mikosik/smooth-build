package org.smoothbuild.slib.bool;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.inst.ValueB;
import org.smoothbuild.bytecode.type.inst.TypeB;

public class False {
  public static ValueB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    return f.bool(false);
  }
}
