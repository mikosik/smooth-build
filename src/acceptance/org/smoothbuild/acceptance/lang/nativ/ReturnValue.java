package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Value;

public class ReturnValue {
  @SmoothFunction
  public static Value returnValue(NativeApi nativeApi) {
    return nativeApi.create().string("abc");
  }
}
