package org.smoothbuild.slib.bool;

import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.plugin.NativeApi;

public class NotFunction {
  public static Bool function(NativeApi nativeApi, Bool value) {
    return nativeApi.factory().bool(!value.jValue());
  }
}
