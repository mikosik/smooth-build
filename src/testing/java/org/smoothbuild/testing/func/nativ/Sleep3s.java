package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class Sleep3s {
  public static InstB func(NativeApi nativeApi, TupleB args) throws InterruptedException {
    Thread.sleep(3000);
    return nativeApi.factory().string("");
  }
}
