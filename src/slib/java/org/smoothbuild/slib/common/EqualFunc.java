package org.smoothbuild.slib.common;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class EqualFunc {
  public static InstB func(NativeApi nativeApi, TupleB args) {
    InstB first = args.get(0);
    InstB second = args.get(1);
    return nativeApi.factory().bool(first.hash().equals(second.hash()));
  }
}
