package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ConcatStrings {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    BString first = (BString) args.get(0);
    BString second = (BString) args.get(1);

    String result = first.toJavaString() + second.toJavaString();
    return nativeApi.factory().string(result);
  }
}
