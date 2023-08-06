package org.smoothbuild.stdlib.bool;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BoolB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class NotFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    BoolB value = (BoolB) args.get(0);
    return nativeApi.factory().bool(!value.toJ());
  }
}
