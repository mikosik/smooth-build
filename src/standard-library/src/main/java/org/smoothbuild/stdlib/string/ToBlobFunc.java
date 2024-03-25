package org.smoothbuild.stdlib.string;

import static org.smoothbuild.common.Constants.CHARSET;

import java.io.IOException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ToBlobFunc {
  public static BValue func(NativeApi nativeApi, BTuple args)
      throws IOException, BytecodeException {
    var stringB = (BString) args.get(0);
    var string = stringB.toJavaString();
    return nativeApi.factory().blob(sink -> sink.writeString(string, CHARSET));
  }
}
