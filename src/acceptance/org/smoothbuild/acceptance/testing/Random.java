package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.plugin.NativeApi;

public class Random {
  public static Str function(NativeApi nativeApi) {
    return nativeApi.factory().string(Integer.toString(new java.util.Random().nextInt()));
  }
}
