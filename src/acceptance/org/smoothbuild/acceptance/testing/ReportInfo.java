package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class ReportInfo {
  @NativeImplementation("reportInfo")
  public static RString reportInfo(NativeApi nativeApi, RString message) {
    nativeApi.log().info(message.jValue());
    return message;
  }
}
