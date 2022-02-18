package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class WrongMethodName {
  public static StringB wrongMethodName(NativeApi nativeApi, TupleB args) {
    return nativeApi.factory().string("abc");
  }
}
