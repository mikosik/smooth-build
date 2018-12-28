package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class ReportWarningAndReturnNull {
  @SmoothFunction("reportWarning")
  public static SString reportWarning(NativeApi nativeApi, SString message) {
    nativeApi.log().warning(message.data());
    return null;
  }
}
