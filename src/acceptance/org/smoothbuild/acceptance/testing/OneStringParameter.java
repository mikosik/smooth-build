package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class OneStringParameter {
  @SmoothFunction("oneStringParameter")
  public static SString oneStringParameter(NativeApi nativeApi, SString string) {
    return string;
  }
}
