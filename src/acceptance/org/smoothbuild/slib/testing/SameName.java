package org.smoothbuild.slib.testing;

import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class SameName {
  @SmoothFunction("sameName")
  public static SString sameName(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
