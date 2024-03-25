package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ThrowRandomException {
  public static BValue func(NativeApi nativeApi, BTuple args) {
    throw new UnsupportedOperationException(Long.toString(System.nanoTime()));
  }
}
