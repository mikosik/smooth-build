package org.smoothbuild.slib.bool;

import org.smoothbuild.bytecode.obj.val.BoolB;
import org.smoothbuild.plugin.NativeApi;

public class NotFunc {
  public static BoolB func(NativeApi nativeApi, BoolB value) {
    return nativeApi.factory().bool(!value.toJ());
  }
}
