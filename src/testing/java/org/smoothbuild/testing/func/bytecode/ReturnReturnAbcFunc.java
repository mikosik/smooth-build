package org.smoothbuild.testing.func.bytecode;

import static org.smoothbuild.util.collect.Lists.list;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.val.InstB;
import org.smoothbuild.bytecode.type.val.TypeB;

public class ReturnReturnAbcFunc {
  public static InstB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    return f.defFunc(f.stringT(), list(), f.string("abc"));
  }
}
