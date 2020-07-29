package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class ThrowRandomException {
  @SmoothFunction("throwRandomException")
  public static RString throwRandomException(NativeApi nativeApi) {
    throw new UnsupportedOperationException(Long.toString(System.nanoTime()));
  }
}
