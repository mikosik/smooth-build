package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class WrongParameterType {
  public static BValue func(NativeApi nativeApi, NativeApi nativeApi2) {
    return null;
  }
}
