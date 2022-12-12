package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.value.TupleB;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.plugin.NativeApi;

public class ReportFixedError {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    nativeApi.log().error("some error message");
    return null;
  }
}
