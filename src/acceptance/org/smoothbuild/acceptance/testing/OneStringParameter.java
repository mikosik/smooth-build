package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.RString;

public class OneStringParameter {
  @SmoothFunction("oneStringParameter")
  public static RString oneStringParameter(NativeApi nativeApi, RString string) {
    return string;
  }
}
