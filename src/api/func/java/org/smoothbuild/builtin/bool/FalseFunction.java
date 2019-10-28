package org.smoothbuild.builtin.bool;

import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class FalseFunction {
  @SmoothFunction("false")
  public static Bool falseFunction(NativeApi nativeApi) {
    return nativeApi.create().bool(false);
  }
}