package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.SString;

public class Sleep3s {
  @SmoothFunction("sleep3s")
  public static SString sleep3s(NativeApi nativeApi) throws InterruptedException {
    Thread.sleep(3000);
    return nativeApi.factory().string("");
  }
}
