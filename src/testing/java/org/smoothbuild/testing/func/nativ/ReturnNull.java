package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.val.InstB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class ReturnNull {
  public static InstB func(NativeApi nativeApi, TupleB args) {
    return null;
  }
}
