package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.bytecode.obj.val.StringB;
import org.smoothbuild.plugin.NativeApi;

public class ReportWarningAndReturnNull {
  public static StringB func(NativeApi nativeApi) {
    nativeApi.log().warning("some warning message");
    return null;
  }
}
