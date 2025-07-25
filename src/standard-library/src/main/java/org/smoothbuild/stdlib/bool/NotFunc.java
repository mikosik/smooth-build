package org.smoothbuild.stdlib.bool;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class NotFunc {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    BBool value = (BBool) args.get(0);
    return nativeApi.factory().bool(!value.toJavaBoolean());
  }
}
