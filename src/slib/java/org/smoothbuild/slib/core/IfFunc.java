package org.smoothbuild.slib.core;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TWO;
import static java.math.BigInteger.ZERO;
import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;

public class IfFunc {
  public static ObjB bytecode(BytecodeF f) {
    var a = f.oVarT("A");
    var type = f.funcT(a, list(f.boolT(), a, a));
    var body = f.if_(
        f.paramRef(f.boolT(), ZERO),
        f.paramRef(f.cVarT("A"), ONE),
        f.paramRef(f.cVarT("A"), TWO));
    return f.func(type, body);
  }
}
