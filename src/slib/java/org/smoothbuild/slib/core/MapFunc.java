package org.smoothbuild.slib.core;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static org.smoothbuild.bytecode.type.val.VarSetB.varSetB;
import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;

/**
 * [R] map([S] array, R(S) mapper)
 */
public class MapFunc {
  public static ObjB bytecode(BytecodeF f) {
    var s = f.varT("S");
    var r = f.varT("R");
    var type = f.funcT(
        varSetB(r, s),
        f.arrayT(r),
        list(f.arrayT(s), f.funcT(varSetB(), r, list(s))));
    var body = f.map(
        f.paramRef(f.arrayT(s), ZERO),
        f.paramRef(f.funcT(varSetB(), r, list(s)), ONE));
    return f.func(type, body);
  }
}
