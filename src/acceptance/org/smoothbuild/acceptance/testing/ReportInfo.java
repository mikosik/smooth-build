package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class ReportInfo {
  @SmoothFunction("reportInfo")
  public static RString reportInfo(NativeApi nativeApi, RString message) {
    nativeApi.log().info(message.jValue());
    return message;
  }
}
