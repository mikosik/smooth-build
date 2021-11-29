package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.plugin.NativeApi;

public class ReportError {
  public static ValueH func(NativeApi nativeApi, StringH message) {
    nativeApi.log().error(message.jValue());
    return null;
  }
}
