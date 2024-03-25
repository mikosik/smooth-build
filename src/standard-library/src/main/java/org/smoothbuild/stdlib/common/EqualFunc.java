package org.smoothbuild.stdlib.common;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class EqualFunc {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    BValue first = args.get(0);
    BValue second = args.get(1);
    return nativeApi.factory().bool(first.hash().equals(second.hash()));
  }
}
