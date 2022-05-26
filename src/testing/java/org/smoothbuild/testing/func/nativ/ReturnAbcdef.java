package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.cnst.StringB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class ReturnAbcdef {
  public static StringB func(NativeApi nativeApi, TupleB args) {
    return nativeApi.factory().string("abcdef");
  }
}
