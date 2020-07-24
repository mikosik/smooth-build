package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class ThrowException {
  @SmoothFunction("throwException")
  public static org.smoothbuild.record.base.Record throwException(NativeApi nativeApi) {
    throw new UnsupportedOperationException();
  }
}
