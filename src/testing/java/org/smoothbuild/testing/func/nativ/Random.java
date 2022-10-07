package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class Random {
  public static InstB func(NativeApi nativeApi, TupleB args) {
    return nativeApi.factory().string(Integer.toString(new java.util.Random().nextInt()));
  }
}
