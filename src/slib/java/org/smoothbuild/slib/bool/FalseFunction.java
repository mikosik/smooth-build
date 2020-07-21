package org.smoothbuild.slib.bool;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.Bool;

public class FalseFunction {
  @SmoothFunction("false")
  public static Bool falseFunction(NativeApi nativeApi) {
    return nativeApi.factory().bool(false);
  }
}
