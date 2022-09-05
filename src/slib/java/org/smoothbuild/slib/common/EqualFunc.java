package org.smoothbuild.slib.common;

import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class EqualFunc {
  public static CnstB func(NativeApi nativeApi, TupleB args) {
    CnstB first = args.get(0);
    CnstB second = args.get(1);
    return nativeApi.factory().bool(first.hash().equals(second.hash()));
  }
}
