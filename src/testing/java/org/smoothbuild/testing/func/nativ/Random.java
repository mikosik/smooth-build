package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class Random {
  public static ValB func(NativeApi nativeApi, TupleB args) {
    return nativeApi.factory().string(Integer.toString(new java.util.Random().nextInt()));
  }
}
