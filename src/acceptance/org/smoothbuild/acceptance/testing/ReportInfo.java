package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.SString;

public class ReportInfo {
  @SmoothFunction("reportInfo")
  public static SString reportInfo(NativeApi nativeApi, SString message) {
    nativeApi.log().info(message.jValue());
    return message;
  }
}
