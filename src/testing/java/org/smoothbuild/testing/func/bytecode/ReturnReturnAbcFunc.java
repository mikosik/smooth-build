package org.smoothbuild.testing.func.bytecode;

import static org.smoothbuild.util.collect.Lists.list;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.inst.ValueB;
import org.smoothbuild.bytecode.type.inst.TypeB;

public class ReturnReturnAbcFunc {
  public static ValueB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    var funcT = f.funcT(list(), f.stringT());
    return f.defFunc(funcT, f.string("abc"));
  }
}
