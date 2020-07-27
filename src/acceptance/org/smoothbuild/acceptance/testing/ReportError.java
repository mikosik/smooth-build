package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.RString;
import org.smoothbuild.record.base.Record;

public class ReportError {
  @SmoothFunction("reportError")
  public static Record reportError(NativeApi nativeApi, RString message) {
    nativeApi.log().error(message.jValue());
    return null;
  }
}
