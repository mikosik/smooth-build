package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.val.InstB;
import org.smoothbuild.plugin.NativeApi;

public class WrongParameterType {
  public static InstB func(NativeApi nativeApi, NativeApi nativeApi2) {
    return null;
  }
}
