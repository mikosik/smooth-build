package org.smoothbuild.slib.bool;

import org.smoothbuild.bytecode.expr.value.BoolB;
import org.smoothbuild.bytecode.expr.value.TupleB;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.plugin.NativeApi;

public class NotFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    BoolB value = (BoolB) args.get(0);
    return nativeApi.factory().bool(!value.toJ());
  }
}
