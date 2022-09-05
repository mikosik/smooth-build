package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class ReturnNull {
  public static CnstB func(NativeApi nativeApi, TupleB args) {
    return null;
  }
}
