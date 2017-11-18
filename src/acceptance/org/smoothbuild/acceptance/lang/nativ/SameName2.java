package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class SameName2 {
  @SmoothFunction
  public static SString sameName(NativeApi nativeApi) {
    return nativeApi.create().string("abc");
  }
}