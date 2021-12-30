package org.smoothbuild.acceptance.testing;

import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.plugin.NativeApi;

public class ReportTwoErrors {
  public static StringB func(NativeApi nativeApi) {
    nativeApi.log().error("first error");
    nativeApi.log().error("second error");
    return null;
  }
}
