package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class ReportFixedError {
  @NativeImplementation("reportFixedError")
  public static Record reportFixedError(NativeApi nativeApi) {
    nativeApi.log().error("some error message");
    return null;
  }
}
