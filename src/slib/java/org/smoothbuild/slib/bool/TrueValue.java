package org.smoothbuild.slib.bool;

import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.plugin.NativeApi;

public class TrueValue {
  public static Bool value(NativeApi nativeApi) {
    return nativeApi.factory().bool(true);
  }
}
