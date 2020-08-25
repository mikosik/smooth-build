package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class ReportTwoErrors {
  @SmoothFunction("reportTwoErrors")
  public static RString reportTwoErrors(NativeApi nativeApi) {
    nativeApi.log().error("first error");
    nativeApi.log().error("second error");
    return null;
  }
}
