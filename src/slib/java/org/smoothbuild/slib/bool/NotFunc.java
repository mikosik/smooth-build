package org.smoothbuild.slib.bool;

import org.smoothbuild.bytecode.obj.cnst.BoolB;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class NotFunc {
  public static CnstB func(NativeApi nativeApi, TupleB args) {
    BoolB value = (BoolB) args.get(0);
    return nativeApi.factory().bool(!value.toJ());
  }
}
