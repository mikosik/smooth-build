package org.smoothbuild.virtualmachine.testing.func.bytecode;

import static org.smoothbuild.common.collect.List.list;

import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

public class ReturnReturnAbcFunc {
  public static BValue bytecode(BytecodeFactory f, Map<String, BType> varMap)
      throws BytecodeException {
    var funcType = f.funcType(list(), f.stringType());
    return f.lambda(funcType, f.string("abc"));
  }
}
