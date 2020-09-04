package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class ReportWarningAndReturnNull {
  @NativeImplementation("reportWarning")
  public static RString reportWarning(NativeApi nativeApi) {
    nativeApi.log().warning("some warning message");
    return null;
  }
}
