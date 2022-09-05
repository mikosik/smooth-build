package org.smoothbuild.slib.bool;

import org.smoothbuild.bytecode.expr.val.BoolB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class NotFunc {
  public static ValB func(NativeApi nativeApi, TupleB args) {
    BoolB value = (BoolB) args.get(0);
    return nativeApi.factory().bool(!value.toJ());
  }
}
