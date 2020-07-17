package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.Nothing;
import org.smoothbuild.record.base.SString;

public class ReportError {
  @SmoothFunction("reportError")
  public static Nothing reportError(NativeApi nativeApi, SString message) {
    nativeApi.log().error(message.jValue());
    return null;
  }
}
