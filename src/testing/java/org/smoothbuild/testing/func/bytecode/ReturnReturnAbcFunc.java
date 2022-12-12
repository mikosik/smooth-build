package org.smoothbuild.testing.func.bytecode;

import static org.smoothbuild.util.collect.Lists.list;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.bytecode.type.value.TypeB;

public class ReturnReturnAbcFunc {
  public static ValueB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    var funcT = f.funcT(list(), f.stringT());
    return f.exprFunc(funcT, f.string("abc"));
  }
}
