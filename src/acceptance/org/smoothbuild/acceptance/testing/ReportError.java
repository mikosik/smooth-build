package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.ValH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.plugin.NativeApi;

public class ReportError {
  public static ValH func(NativeApi nativeApi, StringH message) {
    nativeApi.log().error(message.toJ());
    return null;
  }
}
