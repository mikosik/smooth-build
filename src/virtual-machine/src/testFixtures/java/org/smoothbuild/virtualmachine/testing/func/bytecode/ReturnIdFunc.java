package org.smoothbuild.virtualmachine.testing.func.bytecode;

import static java.math.BigInteger.ZERO;
import static org.smoothbuild.common.collect.List.list;

import java.util.Map;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeF;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

public class ReturnIdFunc {
  public static ValueB bytecode(BytecodeF f, Map<String, TypeB> varMap) throws BytecodeException {
    var a = varMap.get("A");
    var funcT = f.funcT(list(a), a);
    return f.lambda(funcT, f.var(a, ZERO));
  }
}