package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.RString;

public class IllegalName {
  @SmoothFunction("illegalName$")
  public static RString illegalName$(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
