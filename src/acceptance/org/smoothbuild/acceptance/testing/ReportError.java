package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class ReportError {
  @NativeImplementation("reportError")
  public static Obj reportError(NativeApi nativeApi, RString message) {
    nativeApi.log().error(message.jValue());
    return null;
  }
}
