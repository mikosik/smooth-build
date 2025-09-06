package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.jspecify.annotations.Nullable;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ReportError {
  @Nullable
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    BString message = (BString) args.get(0);
    nativeApi.log().error(message.toJavaString());
    return null;
  }
}
