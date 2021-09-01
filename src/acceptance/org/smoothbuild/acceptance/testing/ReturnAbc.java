package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.plugin.NativeApi;

public class ReturnAbc {
  public static Str function(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
