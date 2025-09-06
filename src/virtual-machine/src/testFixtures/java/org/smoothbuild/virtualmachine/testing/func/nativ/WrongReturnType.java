package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.jspecify.annotations.Nullable;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class WrongReturnType {
  @Nullable
  public static BOrder func(NativeApi nativeApi, BTuple args) {
    return null;
  }
}
