package org.smoothbuild.slib.core;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.bytecode.type.val.TypeB;

/**
 * [R] map([S] array, R(S) mapper)
 */
public class MapFunc {
  public static ValB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    var s = varMap.get("S");
    var r = varMap.get("R");
    var type = f.funcT(
        f.arrayT(r),
        list(f.arrayT(s), f.funcT(r, list(s))));
    var body = f.map(
        f.paramRef(f.arrayT(s), ZERO),
        f.paramRef(f.funcT(r, list(s)), ONE));
    return f.func(type, body);
  }
}
