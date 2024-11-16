package org.smoothbuild.stdlib.core;

import java.io.IOException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ErrorFunc {
  public static BValue func(NativeApi nativeApi, BTuple args)
      throws IOException, BytecodeException {
    var messageB = (BString) args.get(0);
    nativeApi.log().error(messageB.toJavaString());
    return null;
  }
}
