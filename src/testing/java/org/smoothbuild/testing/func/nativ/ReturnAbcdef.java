package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.plugin.NativeApi;

public class ReturnAbcdef {
  public static StringB func(NativeApi nativeApi) {
    return nativeApi.factory().string("abcdef");
  }
}
