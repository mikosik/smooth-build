package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SString;

public class ReportError {
  @SmoothFunction("reportError")
  public static Nothing reportError(NativeApi nativeApi, SString message) {
    nativeApi.log().error(message.data());
    return null;
  }
}
