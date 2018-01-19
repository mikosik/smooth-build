package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SString;

public class ReportError {
  @SmoothFunction
  public static Nothing reportError(NativeApi nativeApi, SString message) {
    nativeApi.log().error(message.data());
    return null;
  }
}
