package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.plugin.NativeApi;

public class ReportWarning {
  public static StringH function(NativeApi nativeApi, StringH message) {
    nativeApi.log().warning(message.jValue());
    return message;
  }
}
