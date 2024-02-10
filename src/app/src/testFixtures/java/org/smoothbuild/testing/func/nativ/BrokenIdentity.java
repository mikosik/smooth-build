package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class BrokenIdentity {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    return nativeApi.factory().string("abc");
  }
}
