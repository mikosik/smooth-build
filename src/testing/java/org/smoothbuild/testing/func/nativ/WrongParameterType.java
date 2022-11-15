package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.inst.ValueB;
import org.smoothbuild.plugin.NativeApi;

public class WrongParameterType {
  public static ValueB func(NativeApi nativeApi, NativeApi nativeApi2) {
    return null;
  }
}
