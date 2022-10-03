package org.smoothbuild.slib.bool;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.val.InstB;
import org.smoothbuild.bytecode.type.val.TypeB;

public class True {
  public static InstB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    return f.bool(true);
  }
}
