package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.plugin.NativeApi;

public class WrongParameterType {
  public static StringB func(NativeApi nativeApi, NativeApi nativeApi2) {
    return nativeApi.factory().string("abc");
  }
}
