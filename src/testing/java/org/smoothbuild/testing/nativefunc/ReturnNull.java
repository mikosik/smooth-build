package org.smoothbuild.testing.nativefunc;

import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.plugin.NativeApi;

public class ReturnNull {
  public static StringB func(NativeApi nativeApi) {
    return null;
  }
}
