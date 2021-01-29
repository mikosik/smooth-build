package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;

public class ThrowRandomException {
  public static Str function(NativeApi nativeApi) {
    throw new UnsupportedOperationException(Long.toString(System.nanoTime()));
  }
}
