package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class NonPublicMethod {
  @SmoothFunction("function")
  static RString function(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
