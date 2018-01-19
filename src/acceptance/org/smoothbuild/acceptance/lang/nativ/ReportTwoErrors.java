package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class ReportTwoErrors {
  @SmoothFunction
  public static SString reportTwoErrors(NativeApi nativeApi, SString message1, SString message2) {
    nativeApi.log().error(message1.data());
    nativeApi.log().error(message2.data());
    return null;
  }
}
