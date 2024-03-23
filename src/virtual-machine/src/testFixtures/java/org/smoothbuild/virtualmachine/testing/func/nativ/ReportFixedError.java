package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ReportFixedError {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    nativeApi.log().error("some error message");
    return null;
  }
}
