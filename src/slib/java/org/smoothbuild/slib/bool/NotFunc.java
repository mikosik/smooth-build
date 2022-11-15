package org.smoothbuild.slib.bool;

import org.smoothbuild.bytecode.expr.inst.BoolB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.expr.inst.ValueB;
import org.smoothbuild.plugin.NativeApi;

public class NotFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    BoolB value = (BoolB) args.get(0);
    return nativeApi.factory().bool(!value.toJ());
  }
}
