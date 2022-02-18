package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class ThrowException {
  public static ValB func(NativeApi nativeApi, TupleB args) {
    throw new UnsupportedOperationException();
  }
}
