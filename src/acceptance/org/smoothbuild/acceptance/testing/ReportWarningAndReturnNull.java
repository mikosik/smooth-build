package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.plugin.NativeApi;

public class ReportWarningAndReturnNull {
  public static Str function(NativeApi nativeApi) {
    nativeApi.log().warning("some warning message");
    return null;
  }
}
