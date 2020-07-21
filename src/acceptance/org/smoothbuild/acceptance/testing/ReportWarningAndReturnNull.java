package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.SString;

public class ReportWarningAndReturnNull {
  @SmoothFunction("reportWarning")
  public static SString reportWarning(NativeApi nativeApi, SString message) {
    nativeApi.log().warning(message.jValue());
    return null;
  }
}
