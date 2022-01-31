package org.smoothbuild.nativefunc;

import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class BrokenIdentity {
  public static ValB func(NativeApi nativeApi, ValB val) {
    return nativeApi.factory().string("abc");
  }
}
