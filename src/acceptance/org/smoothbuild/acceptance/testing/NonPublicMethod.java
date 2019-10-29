package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class NonPublicMethod {
  @SmoothFunction("function")
  static SString function(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}