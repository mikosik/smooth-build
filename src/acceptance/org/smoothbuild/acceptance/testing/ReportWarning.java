package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class ReportWarning {
  @NativeImplementation("reportWarning")
  public static Str reportWarning(NativeApi nativeApi, Str message) {
    nativeApi.log().warning(message.jValue());
    return message;
  }
}
