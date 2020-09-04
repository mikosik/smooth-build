package org.smoothbuild.slib.bool;

import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class NotFunction {
  @NativeImplementation("not")
  public static Bool not(NativeApi nativeApi, Bool value) {
    return nativeApi.factory().bool(!value.jValue());
  }
}
