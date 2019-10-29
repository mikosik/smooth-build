package org.smoothbuild.builtin.bool;

import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class NotFunction {
  @SmoothFunction("not")
  public static Bool not(NativeApi nativeApi, Bool value) {
    return nativeApi.factory().bool(!value.data());
  }
}
