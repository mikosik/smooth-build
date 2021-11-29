package org.smoothbuild.slib.bool;

import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.plugin.NativeApi;

public class TrueValue {
  public static BoolH func(NativeApi nativeApi) {
    return nativeApi.factory().bool(true);
  }
}
