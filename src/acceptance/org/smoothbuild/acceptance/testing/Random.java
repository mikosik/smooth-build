package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.plugin.NativeApi;

public class Random {
  public static StringH func(NativeApi nativeApi) {
    return nativeApi.factory().string(Integer.toString(new java.util.Random().nextInt()));
  }
}
