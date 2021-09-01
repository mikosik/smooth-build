package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.plugin.NativeApi;

public class ReportWarning {
  public static Str function(NativeApi nativeApi, Str message) {
    nativeApi.log().warning(message.jValue());
    return message;
  }
}
