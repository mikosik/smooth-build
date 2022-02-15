package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.plugin.NativeApi;

public class ReportWarningAndReturnNull {
  public static StringB func(NativeApi nativeApi) {
    nativeApi.log().warning("some warning message");
    return null;
  }
}
