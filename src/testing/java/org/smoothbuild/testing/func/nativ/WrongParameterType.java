package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class WrongParameterType {
  public static ValueB func(NativeApi nativeApi, NativeApi nativeApi2) {
    return null;
  }
}
