package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class Random {
  public static CnstB func(NativeApi nativeApi, TupleB args) {
    return nativeApi.factory().string(Integer.toString(new java.util.Random().nextInt()));
  }
}
