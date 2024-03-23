package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ReportError {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    BString message = (BString) args.get(0);
    nativeApi.log().error(message.toJavaString());
    return null;
  }
}
