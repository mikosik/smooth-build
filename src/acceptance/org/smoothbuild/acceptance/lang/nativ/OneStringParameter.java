package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class OneStringParameter {
  @SmoothFunction
  public static SString oneStringParameter(NativeApi nativeApi, SString string) {
    return string;
  }
}