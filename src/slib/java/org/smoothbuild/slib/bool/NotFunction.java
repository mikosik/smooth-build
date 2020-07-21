package org.smoothbuild.slib.bool;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.Bool;

public class NotFunction {
  @SmoothFunction("not")
  public static Bool not(NativeApi nativeApi, Bool value) {
    return nativeApi.factory().bool(!value.jValue());
  }
}
