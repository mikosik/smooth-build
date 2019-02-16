package org.smoothbuild.builtin.bool;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Bool;

public class TrueFunction {
  @SmoothFunction("true")
  public static Bool trueFunction(NativeApi nativeApi) {
    return nativeApi.create().bool(true);
  }
}
