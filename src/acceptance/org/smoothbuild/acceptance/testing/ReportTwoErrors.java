package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ReportTwoErrors {
  @SmoothFunction("reportTwoErrors")
  public static SString reportTwoErrors(NativeApi nativeApi, SString message1, SString message2) {
    nativeApi.log().error(message1.jValue());
    nativeApi.log().error(message2.jValue());
    return null;
  }
}
