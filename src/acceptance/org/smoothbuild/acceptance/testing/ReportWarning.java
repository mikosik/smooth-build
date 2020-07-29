package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class ReportWarning {
  @SmoothFunction("reportWarning")
  public static RString reportWarning(NativeApi nativeApi, RString message) {
    nativeApi.log().warning(message.jValue());
    return message;
  }
}
