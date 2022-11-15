package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.expr.inst.ValueB;
import org.smoothbuild.plugin.NativeApi;

public class Random {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    return nativeApi.factory().string(Integer.toString(new java.util.Random().nextInt()));
  }
}
