package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class WrongParameterType {
  public static ValB func(NativeApi nativeApi, NativeApi nativeApi2) {
    return null;
  }
}
