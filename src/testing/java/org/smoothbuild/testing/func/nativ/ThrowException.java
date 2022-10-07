package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class ThrowException {
  public static InstB func(NativeApi nativeApi, TupleB args) {
    throw new UnsupportedOperationException();
  }
}
