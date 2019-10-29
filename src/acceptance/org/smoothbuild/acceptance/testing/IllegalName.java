package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class IllegalName {
  @SmoothFunction("illegalName$")
  public static SString illegalName$(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}