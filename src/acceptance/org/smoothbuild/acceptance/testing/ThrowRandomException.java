package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class ThrowRandomException {
  @NativeImplementation("throwRandomException")
  public static Str throwRandomException(NativeApi nativeApi) {
    throw new UnsupportedOperationException(Long.toString(System.nanoTime()));
  }
}
