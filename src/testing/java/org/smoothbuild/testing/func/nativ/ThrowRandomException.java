package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.value.TupleB;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.plugin.NativeApi;

public class ThrowRandomException {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    throw new UnsupportedOperationException(Long.toString(System.nanoTime()));
  }
}
