package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class ThrowException {
  public static ValB func(NativeApi nativeApi, TupleB args) {
    throw new UnsupportedOperationException();
  }
}
