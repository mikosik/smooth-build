package org.smoothbuild.nativefunc;

import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.plugin.NativeApi;

public class ReportWarning {
  public static StringB func(NativeApi nativeApi, StringB message) {
    nativeApi.log().warning(message.toJ());
    return message;
  }
}