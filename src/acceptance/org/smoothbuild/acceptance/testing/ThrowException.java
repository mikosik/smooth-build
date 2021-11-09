package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.plugin.NativeApi;

public class ThrowException {
  public static ValueH function(NativeApi nativeApi) {
    throw new UnsupportedOperationException();
  }
}
