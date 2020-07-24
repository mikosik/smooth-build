package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.SString;

public class ReportError {
  @SmoothFunction("reportError")
  public static org.smoothbuild.record.base.Record reportError(
      NativeApi nativeApi, SString message) {
    nativeApi.log().error(message.jValue());
    return null;
  }
}
