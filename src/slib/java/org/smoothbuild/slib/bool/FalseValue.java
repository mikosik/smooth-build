package org.smoothbuild.slib.bool;

import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.plugin.NativeApi;

public class FalseValue {
  public static Bool function(NativeApi nativeApi) {
    return nativeApi.factory().bool(false);
  }
}
