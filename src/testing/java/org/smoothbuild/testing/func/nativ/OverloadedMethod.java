package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.plugin.NativeApi;

public class OverloadedMethod {
  public static StringB func(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }

  public static StringB func(NativeApi nativeApi, BlobB blob) {
    return nativeApi.factory().string("abc");
  }
}
