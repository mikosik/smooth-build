package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class Sleep3s {
  @SmoothFunction("sleep3s")
  public static RString sleep3s(NativeApi nativeApi) throws InterruptedException {
    Thread.sleep(3000);
    return nativeApi.factory().string("");
  }
}
