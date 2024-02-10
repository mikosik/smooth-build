package org.smoothbuild.testing.func.bytecode;

import java.util.Map;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

public class ReturnAbc {
  public static ValueB bytecode(BytecodeF bytecodeF, Map<String, TypeB> varMap)
      throws BytecodeException {
    return bytecodeF.string("abc");
  }
}
