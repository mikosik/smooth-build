package org.smoothbuild.slib.bool;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.Bool;

public class FalseFunction {
  @SmoothFunction("false")
  public static Bool falseFunction(NativeApi nativeApi) {
    return nativeApi.factory().bool(false);
  }
}
