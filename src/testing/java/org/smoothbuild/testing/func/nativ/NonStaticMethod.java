package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class NonStaticMethod {
  public ValB func(NativeApi nativeApi, TupleB args) {
    return nativeApi.factory().string("abc");
  }
}
