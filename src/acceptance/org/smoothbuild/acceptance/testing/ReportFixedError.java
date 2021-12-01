package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.plugin.NativeApi;

public class ReportFixedError {
  public static ValH func(NativeApi nativeApi) {
    nativeApi.log().error("some error message");
    return null;
  }
}
