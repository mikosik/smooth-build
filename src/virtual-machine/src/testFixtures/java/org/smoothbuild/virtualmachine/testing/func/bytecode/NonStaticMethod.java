package org.smoothbuild.virtualmachine.testing.func.bytecode;

import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

public class NonStaticMethod {
  public ValueB bytecode(BytecodeFactory bytecodeFactory, Map<String, TypeB> varMap)
      throws BytecodeException {
    return bytecodeFactory.string("abc");
  }
}
