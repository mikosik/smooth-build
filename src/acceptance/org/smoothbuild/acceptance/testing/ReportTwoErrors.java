package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.RString;

public class ReportTwoErrors {
  @SmoothFunction("reportTwoErrors")
  public static RString reportTwoErrors(NativeApi nativeApi, RString message1, RString message2) {
    nativeApi.log().error(message1.jValue());
    nativeApi.log().error(message2.jValue());
    return null;
  }
}
