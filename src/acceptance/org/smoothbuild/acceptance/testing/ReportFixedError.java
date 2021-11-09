package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.plugin.NativeApi;

public class ReportFixedError {
  public static ValueH function(NativeApi nativeApi) {
    nativeApi.log().error("some error message");
    return null;
  }
}
