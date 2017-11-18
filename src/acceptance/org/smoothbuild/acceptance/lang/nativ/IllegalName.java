package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class IllegalName {
  @SmoothFunction
  public static SString illegalName$(NativeApi nativeApi) {
    return nativeApi.create().string("abc");
  }
}