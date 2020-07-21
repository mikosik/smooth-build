package org.smoothbuild.slib.bool;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.Bool;

public class TrueFunction {
  @SmoothFunction("true")
  public static Bool trueFunction(NativeApi nativeApi) {
    return nativeApi.factory().bool(true);
  }
}
