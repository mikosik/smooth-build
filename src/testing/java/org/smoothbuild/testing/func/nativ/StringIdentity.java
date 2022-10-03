package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.val.InstB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class StringIdentity {
  public static InstB func(NativeApi nativeApi, TupleB args) {
    return args.get(0);
  }
}
