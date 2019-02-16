package org.smoothbuild.builtin.bool;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Bool;

public class FalseFunction {
  @SmoothFunction("false")
  public static Bool falseFunction(NativeApi nativeApi) {
    return nativeApi.create().bool(false);
  }
}