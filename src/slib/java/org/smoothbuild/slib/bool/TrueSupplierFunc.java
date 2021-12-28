package org.smoothbuild.slib.bool;

import org.smoothbuild.db.bytecode.obj.val.BoolB;
import org.smoothbuild.plugin.NativeApi;

public class TrueSupplierFunc {
  public static BoolB func(NativeApi nativeApi) {
    return nativeApi.factory().bool(true);
  }
}
