package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.expr.oper.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class WrongReturnType {
  public static BOrder func(NativeApi nativeApi, BTuple args) {
    return null;
  }
}
