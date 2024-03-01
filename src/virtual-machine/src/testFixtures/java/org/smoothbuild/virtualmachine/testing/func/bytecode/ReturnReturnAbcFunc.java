package org.smoothbuild.virtualmachine.testing.func.bytecode;

import static org.smoothbuild.common.collect.List.list;

import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeF;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

public class ReturnReturnAbcFunc {
  public static ValueB bytecode(BytecodeF f, Map<String, TypeB> varMap) throws BytecodeException {
    var funcT = f.funcT(list(), f.stringT());
    return f.lambda(funcT, f.string("abc"));
  }
}