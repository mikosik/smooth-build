package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class OverloadedMethod {
  public static StringB func(NativeApi nativeApi, TupleB args) {
    return nativeApi.factory().string("abc");
  }

  public static StringB func(NativeApi nativeApi, TupleB args, BlobB blob) {
    return nativeApi.factory().string("abc");
  }
}