package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.ValH;
import org.smoothbuild.plugin.NativeApi;

public class ThrowException {
  public static ValH func(NativeApi nativeApi) {
    throw new UnsupportedOperationException();
  }
}
