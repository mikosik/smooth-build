package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.plugin.NativeApi;

public class WrongReturnType {
  public static OrderB func(NativeApi nativeApi, TupleB args) {
    return null;
  }
}
