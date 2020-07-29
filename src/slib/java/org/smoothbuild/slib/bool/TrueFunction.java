package org.smoothbuild.slib.bool;

import org.smoothbuild.db.record.base.Bool;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class TrueFunction {
  @SmoothFunction("true")
  public static Bool trueFunction(NativeApi nativeApi) {
    return nativeApi.factory().bool(true);
  }
}
