package org.smoothbuild.slib.bool;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.bytecode.type.val.TypeB;

public class True {
  public static ValB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    return f.bool(true);
  }
}
