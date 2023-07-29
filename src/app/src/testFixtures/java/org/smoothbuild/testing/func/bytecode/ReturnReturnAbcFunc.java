package org.smoothbuild.testing.func.bytecode;

import static org.smoothbuild.common.collect.Lists.list;

import java.util.Map;

import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

public class ReturnReturnAbcFunc {
  public static ValueB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    var funcT = f.funcT(list(), f.stringT());
    return f.lambda(funcT, f.string("abc"));
  }
}
