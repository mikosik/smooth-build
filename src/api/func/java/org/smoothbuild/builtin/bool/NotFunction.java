package org.smoothbuild.builtin.bool;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Bool;

public class NotFunction {
  @SmoothFunction("not")
  public static Bool not(NativeApi nativeApi, Bool value) {
    return nativeApi.create().bool(!value.data());
  }
}
