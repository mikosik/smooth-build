package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.bytecode.obj.val.StringB;
import org.smoothbuild.db.bytecode.obj.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class ReportError {
  public static ValB func(NativeApi nativeApi, StringB message) {
    nativeApi.log().error(message.toJ());
    return null;
  }
}
