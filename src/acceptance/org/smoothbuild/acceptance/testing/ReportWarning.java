package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.SString;

public class ReportWarning {
  @SmoothFunction("reportWarning")
  public static SString reportWarning(NativeApi nativeApi, SString message) {
    nativeApi.log().warning(message.jValue());
    return message;
  }
}
