package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class ThrowRandomException {
  @SmoothFunction("throwRandomException")
  public static SString throwRandomException(NativeApi nativeApi) {
    throw new UnsupportedOperationException(Long.toString(System.nanoTime()));
  }
}
