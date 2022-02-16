package org.smoothbuild.slib.core;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;

/**
 * [R] map([S] array, R(S) mapper)
 */
public class MapFunc {
  public static ObjB bytecode(BytecodeF f) {
    var s = f.oVarT("S");
    var r = f.oVarT("R");
    var type = f.funcT(f.arrayT(r), list(f.arrayT(s), f.funcT(r, list(s))));
    var body = f.map(
        f.paramRef(f.arrayT(f.cVarT("S")), ZERO),
        f.paramRef(f.funcT(f.cVarT("R"), list(f.cVarT("S"))), ONE));
    return f.func(type, body);
  }
}
