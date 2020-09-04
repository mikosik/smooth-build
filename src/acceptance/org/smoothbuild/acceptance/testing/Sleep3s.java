package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class Sleep3s {
  @NativeImplementation("sleep3s")
  public static RString sleep3s(NativeApi nativeApi) throws InterruptedException {
    Thread.sleep(3000);
    return nativeApi.factory().string("");
  }
}
