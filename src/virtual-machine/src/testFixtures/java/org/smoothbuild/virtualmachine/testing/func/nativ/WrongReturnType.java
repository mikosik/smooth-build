package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.expr.oper.OrderB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class WrongReturnType {
  public static OrderB func(NativeApi nativeApi, TupleB args) {
    return null;
  }
}
