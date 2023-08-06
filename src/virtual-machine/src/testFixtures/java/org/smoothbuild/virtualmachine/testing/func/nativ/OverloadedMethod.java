package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class OverloadedMethod {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    return nativeApi.factory().string("abc");
  }

  public static ValueB func(NativeApi nativeApi, TupleB args, BlobB blob) throws BytecodeException {
    return nativeApi.factory().string("abc");
  }
}
