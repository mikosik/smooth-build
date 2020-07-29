package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class SameName2 {
  @SmoothFunction("sameName")
  public static RString sameName(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
