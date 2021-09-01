package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.plugin.NativeApi;

public class ReportFixedError {
  public static Val function(NativeApi nativeApi) {
    nativeApi.log().error("some error message");
    return null;
  }
}
