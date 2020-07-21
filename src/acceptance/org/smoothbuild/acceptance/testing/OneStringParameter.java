package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.SString;

public class OneStringParameter {
  @SmoothFunction("oneStringParameter")
  public static SString oneStringParameter(NativeApi nativeApi, SString string) {
    return string;
  }
}
