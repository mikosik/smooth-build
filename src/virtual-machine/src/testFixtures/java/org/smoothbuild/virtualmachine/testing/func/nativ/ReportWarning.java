package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ReportWarning {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    BString message = (BString) args.get(0);
    nativeApi.log().warning(message.toJavaString());
    return message;
  }
}
