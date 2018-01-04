package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.message.WarningMessage;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class ReportWarningAndReturnNull {
  @SmoothFunction
  public static SString reportWarning(NativeApi nativeApi, SString message) {
    nativeApi.log(new WarningMessage(message.data()));
    return null;
  }
}
