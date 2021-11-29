package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.plugin.NativeApi;

public class ReportWarningAndReturnNull {
  public static StringH func(NativeApi nativeApi) {
    nativeApi.log().warning("some warning message");
    return null;
  }
}
