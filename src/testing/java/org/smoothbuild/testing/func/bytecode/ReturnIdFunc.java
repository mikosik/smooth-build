package org.smoothbuild.testing.func.bytecode;

import static java.math.BigInteger.ZERO;
import static org.smoothbuild.bytecode.type.val.VarSetB.varSetB;
import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;

public class ReturnIdFunc {
  public static ObjB bytecode(BytecodeF f) {
    var a = f.varT("A");
    var type = f.funcT(varSetB(a), a, list(a));
    return f.func(type, f.paramRef(f.varT("A"), ZERO));
  }
}
