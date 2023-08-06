package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class WrongParameterType {
  public static ValueB func(NativeApi nativeApi, NativeApi nativeApi2) {
    return null;
  }
}
