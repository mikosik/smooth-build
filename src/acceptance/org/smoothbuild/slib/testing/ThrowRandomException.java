package org.smoothbuild.slib.testing;

import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ThrowRandomException {
  @SmoothFunction("throwRandomException")
  public static SString throwRandomException(NativeApi nativeApi) {
    throw new UnsupportedOperationException(Long.toString(System.nanoTime()));
  }
}
