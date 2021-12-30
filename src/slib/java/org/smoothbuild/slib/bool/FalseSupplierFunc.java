package org.smoothbuild.slib.bool;

import org.smoothbuild.bytecode.obj.val.BoolB;
import org.smoothbuild.plugin.NativeApi;

public class FalseSupplierFunc {
  public static BoolB func(NativeApi nativeApi) {
    return nativeApi.factory().bool(false);
  }
}
