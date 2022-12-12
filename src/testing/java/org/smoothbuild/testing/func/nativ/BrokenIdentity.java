package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.value.TupleB;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.plugin.NativeApi;

public class BrokenIdentity {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    return nativeApi.factory().string("abc");
  }
}
