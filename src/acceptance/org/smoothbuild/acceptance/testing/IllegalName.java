package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.SString;

public class IllegalName {
  @SmoothFunction("illegalName$")
  public static SString illegalName$(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}
