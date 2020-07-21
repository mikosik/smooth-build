package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.SString;

public class SameName2 {
  @SmoothFunction("sameName")
  public static SString sameName(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
