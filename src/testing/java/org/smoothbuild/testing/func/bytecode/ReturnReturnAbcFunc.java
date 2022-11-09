package org.smoothbuild.testing.func.bytecode;

import static org.smoothbuild.util.collect.Lists.list;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.type.inst.TypeB;

public class ReturnReturnAbcFunc {
  public static InstB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    var funcT = f.funcT(f.stringT(), list());
    return f.defFunc(funcT, f.string("abc"));
  }
}
