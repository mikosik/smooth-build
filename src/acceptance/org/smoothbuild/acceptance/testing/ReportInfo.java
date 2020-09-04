package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class ReportInfo {
  @NativeImplementation("reportInfo")
  public static Str reportInfo(NativeApi nativeApi, Str message) {
    nativeApi.log().info(message.jValue());
    return message;
  }
}
