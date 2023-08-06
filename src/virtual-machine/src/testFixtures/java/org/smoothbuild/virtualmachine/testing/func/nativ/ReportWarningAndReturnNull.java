package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ReportWarningAndReturnNull {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    nativeApi.log().warning("some warning message");
    return null;
  }
}
