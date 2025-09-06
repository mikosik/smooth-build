package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.jspecify.annotations.Nullable;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ReportWarningAndReturnNull {
  @Nullable
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    nativeApi.log().warning("some warning message");
    return null;
  }
}
