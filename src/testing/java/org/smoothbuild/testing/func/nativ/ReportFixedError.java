package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class ReportFixedError {
  public static ValB func(NativeApi nativeApi, TupleB args) {
    nativeApi.log().error("some error message");
    return null;
  }
}
