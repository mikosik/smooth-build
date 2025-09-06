package org.smoothbuild.stdlib.core;

import java.io.IOException;
import org.jspecify.annotations.Nullable;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ErrorFunc {
  @Nullable
  public static BValue func(NativeApi nativeApi, BTuple args) throws IOException {
    var messageB = (BString) args.get(0);
    nativeApi.log().error(messageB.toJavaString());
    return null;
  }
}
