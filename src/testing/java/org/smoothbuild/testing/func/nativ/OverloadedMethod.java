package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.inst.BlobB;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class OverloadedMethod {
  public static InstB func(NativeApi nativeApi, TupleB args) {
    return nativeApi.factory().string("abc");
  }

  public static InstB func(NativeApi nativeApi, TupleB args, BlobB blob) {
    return nativeApi.factory().string("abc");
  }
}
