package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.bytecode.obj.expr.OrderB;
import org.smoothbuild.plugin.NativeApi;

public class WrongReturnType {
  public static OrderB func(NativeApi nativeApi, TupleB args) {
    return null;
  }
}
