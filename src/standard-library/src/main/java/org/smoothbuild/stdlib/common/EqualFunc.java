package org.smoothbuild.stdlib.common;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class EqualFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    ValueB first = args.get(0);
    ValueB second = args.get(1);
    return nativeApi.factory().bool(first.hash().equals(second.hash()));
  }
}
