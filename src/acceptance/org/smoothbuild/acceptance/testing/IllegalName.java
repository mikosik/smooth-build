package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class IllegalName {
  @SmoothFunction("illegalName$")
  public static RString illegalName$(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
