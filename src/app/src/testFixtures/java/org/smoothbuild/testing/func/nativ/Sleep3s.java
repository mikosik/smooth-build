package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class Sleep3s {
  public static ValueB func(NativeApi nativeApi, TupleB args)
      throws InterruptedException, BytecodeException {
    Thread.sleep(3000);
    return nativeApi.factory().string("");
  }
}
