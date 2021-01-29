package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;

public class ReportTwoErrors {
  public static Str function(NativeApi nativeApi) {
    nativeApi.log().error("first error");
    nativeApi.log().error("second error");
    return null;
  }
}
