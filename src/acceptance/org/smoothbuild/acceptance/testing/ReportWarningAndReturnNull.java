package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class ReportWarningAndReturnNull {
  @SmoothFunction("reportWarning")
  public static RString reportWarning(NativeApi nativeApi) {
    nativeApi.log().warning("some warning message");
    return null;
  }
}
