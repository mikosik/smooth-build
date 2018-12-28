package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Value;

public class GenericResult {
  @SmoothFunction("genericResult")
  public static Value genericResult(NativeApi nativeApi, Array array) {
    return nativeApi.create().string("abc");
  }
}
