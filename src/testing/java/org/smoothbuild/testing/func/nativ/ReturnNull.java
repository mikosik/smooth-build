package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class ReturnNull {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    return null;
  }
}
