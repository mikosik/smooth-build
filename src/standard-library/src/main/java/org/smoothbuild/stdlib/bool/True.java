package org.smoothbuild.stdlib.bool;

import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

public class True {
  public static ValueB bytecode(BytecodeFactory f, Map<String, TypeB> varMap)
      throws BytecodeException {
    return f.bool(true);
  }
}
