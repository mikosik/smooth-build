package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.Nothing;
import org.smoothbuild.lang.value.SString;

public class ThrowException {
  @SmoothFunction("throwException")
  public static Nothing throwException(NativeApi nativeApi) {
    throw new UnsupportedOperationException();
  }
}
