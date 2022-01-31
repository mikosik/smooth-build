package org.smoothbuild.nativefunc;

import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.plugin.NativeApi;

public class WrongMethodName {
  public static StringB wrongMethodName(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
