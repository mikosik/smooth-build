package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.StringB;
import org.smoothbuild.plugin.NativeApi;

public class Sleep3s {
  public static StringB func(NativeApi nativeApi) throws InterruptedException {
    Thread.sleep(3000);
    return nativeApi.factory().string("");
  }
}
