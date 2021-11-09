package org.smoothbuild.slib.bool;

import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.plugin.NativeApi;

public class NotFunction {
  public static BoolH function(NativeApi nativeApi, BoolH value) {
    return nativeApi.factory().bool(!value.jValue());
  }
}
