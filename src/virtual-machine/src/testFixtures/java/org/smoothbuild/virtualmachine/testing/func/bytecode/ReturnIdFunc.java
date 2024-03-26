package org.smoothbuild.virtualmachine.testing.func.bytecode;

import static java.math.BigInteger.ZERO;
import static org.smoothbuild.common.collect.List.list;

import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

public class ReturnIdFunc {
  public static BValue bytecode(BytecodeFactory f, Map<String, BType> varMap)
      throws BytecodeException {
    var a = varMap.get("A");
    var funcType = f.funcType(list(a), a);
    return f.lambda(funcType, f.reference(a, f.int_(ZERO)));
  }
}
