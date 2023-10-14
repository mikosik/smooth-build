package org.smoothbuild.testing.func.bytecode;

import static java.math.BigInteger.ZERO;

import java.util.List;
import java.util.Map;

import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

public class ReturnIdFunc {
  public static ValueB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    var a = varMap.get("A");
    var funcT = f.funcT(List.of(a), a);
    return f.lambda(funcT, f.var(a, ZERO));
  }
}
