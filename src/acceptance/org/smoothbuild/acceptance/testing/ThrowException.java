package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.object.base.Nothing;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ThrowException {
  @SmoothFunction("throwException")
  public static Nothing throwException(NativeApi nativeApi) {
    throw new UnsupportedOperationException();
  }
}
