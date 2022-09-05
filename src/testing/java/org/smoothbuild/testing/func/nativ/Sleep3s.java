package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class Sleep3s {
  public static CnstB func(NativeApi nativeApi, TupleB args) throws InterruptedException {
    Thread.sleep(3000);
    return nativeApi.factory().string("");
  }
}
