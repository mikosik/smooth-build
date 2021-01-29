package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.plugin.NativeApi;

public class ReportFixedError {
  public static Obj function(NativeApi nativeApi) {
    nativeApi.log().error("some error message");
    return null;
  }
}
