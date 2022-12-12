package org.smoothbuild.slib.common;

import org.smoothbuild.bytecode.expr.value.TupleB;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.plugin.NativeApi;

public class EqualFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    ValueB first = args.get(0);
    ValueB second = args.get(1);
    return nativeApi.factory().bool(first.hash().equals(second.hash()));
  }
}
