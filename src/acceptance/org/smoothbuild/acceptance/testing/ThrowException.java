package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class ThrowException {
  @SmoothFunction("throwException")
  public static Record throwException(NativeApi nativeApi) {
    throw new UnsupportedOperationException();
  }
}
