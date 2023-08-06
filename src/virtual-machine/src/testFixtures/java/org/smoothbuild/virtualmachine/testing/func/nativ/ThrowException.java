package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ThrowException {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    throw new UnsupportedOperationException();
  }
}
