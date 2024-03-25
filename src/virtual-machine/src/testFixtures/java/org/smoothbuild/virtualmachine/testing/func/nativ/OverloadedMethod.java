package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class OverloadedMethod {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    return nativeApi.factory().string("abc");
  }

  public static BValue func(NativeApi nativeApi, BTuple args, BBlob blob) throws BytecodeException {
    return nativeApi.factory().string("abc");
  }
}
