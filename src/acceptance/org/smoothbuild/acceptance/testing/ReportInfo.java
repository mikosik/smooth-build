package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;

public class ReportInfo {
  public static Str function(NativeApi nativeApi, Str message) {
    nativeApi.log().info(message.jValue());
    return message;
  }
}
