package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;

public class ReportError {
  public static Obj function(NativeApi nativeApi, Str message) {
    nativeApi.log().error(message.jValue());
    return null;
  }
}
