package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.SString;

public class NonPublicMethod {
  @SmoothFunction("function")
  static SString function(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
