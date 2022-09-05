package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.cnst.BlobB;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class OverloadedMethod {
  public static CnstB func(NativeApi nativeApi, TupleB args) {
    return nativeApi.factory().string("abc");
  }

  public static CnstB func(NativeApi nativeApi, TupleB args, BlobB blob) {
    return nativeApi.factory().string("abc");
  }
}
