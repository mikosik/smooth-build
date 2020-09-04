package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class ReportWarningAndReturnNull {
  @NativeImplementation("reportWarning")
  public static Str reportWarning(NativeApi nativeApi) {
    nativeApi.log().warning("some warning message");
    return null;
  }
}
