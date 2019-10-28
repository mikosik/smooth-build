package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ReportWarningAndReturnNull {
  @SmoothFunction("reportWarning")
  public static SString reportWarning(NativeApi nativeApi, SString message) {
    nativeApi.log().warning(message.data());
    return null;
  }
}
