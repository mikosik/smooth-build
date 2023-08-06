package org.smoothbuild.virtualmachine.testing.func.bytecode;

import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeF;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

public class NonStaticMethod {
  public ValueB bytecode(BytecodeF bytecodeF, Map<String, TypeB> varMap) throws BytecodeException {
    return bytecodeF.string("abc");
  }
}
