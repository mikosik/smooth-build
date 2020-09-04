package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class ReportFixedError {
  @NativeImplementation("reportFixedError")
  public static Obj reportFixedError(NativeApi nativeApi) {
    nativeApi.log().error("some error message");
    return null;
  }
}
