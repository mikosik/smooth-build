package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.value.TupleB;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.plugin.NativeApi;

public class Sleep3s {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws InterruptedException {
    Thread.sleep(3000);
    return nativeApi.factory().string("");
  }
}
