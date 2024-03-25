package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class Sleep3s {
  public static BValue func(NativeApi nativeApi, BTuple args)
      throws InterruptedException, BytecodeException {
    Thread.sleep(3000);
    return nativeApi.factory().string("");
  }
}
