package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Nothing;

public class ThrowException {
  @SmoothFunction("throwException")
  public static Nothing throwException(NativeApi nativeApi) {
    throw new UnsupportedOperationException();
  }
}
