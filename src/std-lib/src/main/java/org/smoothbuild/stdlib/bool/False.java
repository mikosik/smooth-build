package org.smoothbuild.stdlib.bool;

import java.util.Map;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

public class False {
  public static ValueB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    return f.bool(false);
  }
}
