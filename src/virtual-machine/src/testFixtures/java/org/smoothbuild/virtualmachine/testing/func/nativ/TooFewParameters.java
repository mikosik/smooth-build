package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.jspecify.annotations.Nullable;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class TooFewParameters {
  @Nullable
  public static BValue func(NativeApi nativeApi) {
    return null;
  }
}
