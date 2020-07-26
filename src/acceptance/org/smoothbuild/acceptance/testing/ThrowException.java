package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.Record;

public class ThrowException {
  @SmoothFunction("throwException")
  public static Record throwException(NativeApi nativeApi) {
    throw new UnsupportedOperationException();
  }
}
