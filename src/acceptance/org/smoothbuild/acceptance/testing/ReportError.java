package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.object.base.Nothing;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ReportError {
  @SmoothFunction("reportError")
  public static Nothing reportError(NativeApi nativeApi, SString message) {
    nativeApi.log().error(message.data());
    return null;
  }
}
