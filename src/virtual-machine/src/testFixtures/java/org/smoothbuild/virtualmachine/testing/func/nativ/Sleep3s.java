package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class Sleep3s {
  public static ValueB func(NativeApi nativeApi, TupleB args)
      throws InterruptedException, BytecodeException {
    Thread.sleep(3000);
    return nativeApi.factory().string("");
  }
}
