package org.smoothbuild.slib.bool;

import org.smoothbuild.db.record.base.Bool;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class FalseFunction {
  @SmoothFunction("false")
  public static Bool falseFunction(NativeApi nativeApi) {
    return nativeApi.factory().bool(false);
  }
}
