package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.bytecode.obj.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class ThrowException {
  public static ValB func(NativeApi nativeApi) {
    throw new UnsupportedOperationException();
  }
}
