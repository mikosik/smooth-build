package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ReportErrorAndReturnNonNull {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    nativeApi.log().error("some error message");
    return nativeApi.factory().string("abc");
  }
}