package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class WrongReturnType {
  public static OrderB func(NativeApi nativeApi, TupleB args) {
    return null;
  }
}
