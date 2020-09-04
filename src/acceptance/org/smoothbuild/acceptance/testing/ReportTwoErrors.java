package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class ReportTwoErrors {
  @NativeImplementation("reportTwoErrors")
  public static Str reportTwoErrors(NativeApi nativeApi) {
    nativeApi.log().error("first error");
    nativeApi.log().error("second error");
    return null;
  }
}
