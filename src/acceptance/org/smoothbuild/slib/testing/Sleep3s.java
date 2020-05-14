package org.smoothbuild.slib.testing;

import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class Sleep3s {
  @SmoothFunction("sleep3s")
  public static SString sleep3s(NativeApi nativeApi) {
    return nativeApi.factory().string("");
  }
}
