package org.smoothbuild.testing.nativefunc;

import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class ThrowException {
  public static ValB func(NativeApi nativeApi) {
    throw new UnsupportedOperationException();
  }
}
