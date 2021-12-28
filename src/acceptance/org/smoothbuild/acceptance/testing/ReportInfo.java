package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.bytecode.obj.val.StringB;
import org.smoothbuild.plugin.NativeApi;

public class ReportInfo {
  public static StringB func(NativeApi nativeApi, StringB message) {
    nativeApi.log().info(message.toJ());
    return message;
  }
}
