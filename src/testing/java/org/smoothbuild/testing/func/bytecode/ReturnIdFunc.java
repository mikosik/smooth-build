package org.smoothbuild.testing.func.bytecode;

import static java.math.BigInteger.ZERO;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.bytecode.type.val.TypeB;

public class ReturnIdFunc {
  public static ValB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    var a = varMap.get("A");
    return f.defFunc(a, list(a), f.ref(a, ZERO));
  }
}
