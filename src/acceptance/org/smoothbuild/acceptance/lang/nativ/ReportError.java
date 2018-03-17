package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;

public class ReportError {
  @SmoothFunction
  public static Value reportError(NativeApi nativeApi, SString message) {
    nativeApi.log().error(message.data());
    return null;
  }
}
