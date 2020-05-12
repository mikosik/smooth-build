package org.smoothbuild.slib.bool;

import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class TrueFunction {
  @SmoothFunction("true")
  public static Bool trueFunction(NativeApi nativeApi) {
    return nativeApi.factory().bool(true);
  }
}
