package org.smoothbuild.nativefunc;

import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.plugin.NativeApi;

public class ReturnAbc {
  public static StringB func(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
