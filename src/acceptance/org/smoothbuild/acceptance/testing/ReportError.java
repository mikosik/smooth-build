package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class ReportError {
  @SmoothFunction("reportError")
  public static Record reportError(NativeApi nativeApi, RString message) {
    nativeApi.log().error(message.jValue());
    return null;
  }
}
