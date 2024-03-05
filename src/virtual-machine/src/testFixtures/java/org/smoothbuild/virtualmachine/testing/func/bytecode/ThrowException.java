package org.smoothbuild.virtualmachine.testing.func.bytecode;

import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

public class ThrowException {
  public static ValueB bytecode(BytecodeFactory bytecodeFactory, Map<String, TypeB> varMap) {
    throw new UnsupportedOperationException("detailed message");
  }
}
