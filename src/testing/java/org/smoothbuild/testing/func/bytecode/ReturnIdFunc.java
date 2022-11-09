package org.smoothbuild.testing.func.bytecode;

import static java.math.BigInteger.ZERO;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.type.inst.TypeB;

public class ReturnIdFunc {
  public static InstB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    var a = varMap.get("A");
    var funcT = f.funcT(a, list(a));
    return f.defFunc(funcT, f.ref(a, ZERO));
  }
}
