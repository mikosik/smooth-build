package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class Sleep3s {
  public static ValB func(NativeApi nativeApi, TupleB args) throws InterruptedException {
    Thread.sleep(3000);
    return nativeApi.factory().string("");
  }
}
