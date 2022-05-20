package org.smoothbuild.slib.core;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TWO;
import static java.math.BigInteger.ZERO;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.type.cnst.TypeB;

public class IfFunc {
  public static ObjB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    var a = varMap.get("A");
    var type = f.funcT(a, list(f.boolT(), a, a));
    var body = f.if_(
        f.paramRef(f.boolT(), ZERO),
        f.paramRef(a, ONE),
        f.paramRef(a, TWO));
    return f.func(type, body);
  }
}
