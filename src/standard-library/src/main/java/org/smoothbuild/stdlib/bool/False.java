package org.smoothbuild.stdlib.bool;

import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeF;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

public class False {
  public static ValueB bytecode(BytecodeF f, Map<String, TypeB> varMap) throws BytecodeException {
    return f.bool(false);
  }
}
