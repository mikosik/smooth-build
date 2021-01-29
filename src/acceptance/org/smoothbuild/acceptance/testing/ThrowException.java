package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.plugin.NativeApi;

public class ThrowException {
  public static Obj function(NativeApi nativeApi) {
    throw new UnsupportedOperationException();
  }
}
