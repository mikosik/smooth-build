package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ReportInfo {
  @SmoothFunction("reportInfo")
  public static SString reportInfo(NativeApi nativeApi, SString message) {
    nativeApi.log().info(message.jValue());
    return message;
  }
}
