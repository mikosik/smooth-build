package org.smoothbuild.virtualmachine.testing.func.bytecode;

import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructArray;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

public class WithNonValueResult {
  @Nullable
  public static BConstructArray bytecode(
      BytecodeFactory bytecodeFactory, Map<String, BType> varMap) {
    return null;
  }
}
