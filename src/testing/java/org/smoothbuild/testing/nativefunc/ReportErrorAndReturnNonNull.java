package org.smoothbuild.testing.nativefunc;

import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.plugin.NativeApi;

public class ReportErrorAndReturnNonNull {
  public static StringB func(NativeApi nativeApi) {
    nativeApi.log().error("some error message");
    return nativeApi.factory().string("abc");
  }
}
