package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.RString;

public class ReportWarningAndReturnNull {
  @SmoothFunction("reportWarning")
  public static RString reportWarning(NativeApi nativeApi, RString message) {
    nativeApi.log().warning(message.jValue());
    return null;
  }
}
