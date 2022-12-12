package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.value.BlobB;
import org.smoothbuild.bytecode.expr.value.TupleB;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.plugin.NativeApi;

public class OverloadedMethod {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    return nativeApi.factory().string("abc");
  }

  public static ValueB func(NativeApi nativeApi, TupleB args, BlobB blob) {
    return nativeApi.factory().string("abc");
  }
}
