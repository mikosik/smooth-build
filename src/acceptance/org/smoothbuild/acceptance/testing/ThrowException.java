package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Val;
import org.smoothbuild.plugin.NativeApi;

public class ThrowException {
  public static Val function(NativeApi nativeApi) {
    throw new UnsupportedOperationException();
  }
}
