package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.plugin.NativeApi;

public class ReportError {
  public static Val function(NativeApi nativeApi, Str message) {
    nativeApi.log().error(message.jValue());
    return null;
  }
}
