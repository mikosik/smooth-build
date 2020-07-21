package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.SString;

public class ReportTwoErrors {
  @SmoothFunction("reportTwoErrors")
  public static SString reportTwoErrors(NativeApi nativeApi, SString message1, SString message2) {
    nativeApi.log().error(message1.jValue());
    nativeApi.log().error(message2.jValue());
    return null;
  }
}
