package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class Random {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    return nativeApi.factory().string(Integer.toString(new java.util.Random().nextInt()));
  }
}
