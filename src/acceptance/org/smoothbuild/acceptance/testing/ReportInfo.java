package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.RString;

public class ReportInfo {
  @SmoothFunction("reportInfo")
  public static RString reportInfo(NativeApi nativeApi, RString message) {
    nativeApi.log().info(message.jValue());
    return message;
  }
}
