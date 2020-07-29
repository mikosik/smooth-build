package org.smoothbuild.slib.bool;

import org.smoothbuild.db.record.base.Bool;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class NotFunction {
  @SmoothFunction("not")
  public static Bool not(NativeApi nativeApi, Bool value) {
    return nativeApi.factory().bool(!value.jValue());
  }
}
