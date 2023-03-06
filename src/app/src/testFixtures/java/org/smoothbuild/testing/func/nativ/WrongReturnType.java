package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.vm.bytecode.expr.oper.OrderB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class WrongReturnType {
  public static OrderB func(NativeApi nativeApi, TupleB args) {
    return null;
  }
}
