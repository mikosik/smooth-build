package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.RString;

public class SameName {
  @SmoothFunction("sameName")
  public static RString sameName(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
