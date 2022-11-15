package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.expr.inst.ValueB;
import org.smoothbuild.plugin.NativeApi;

public class ReportErrorAndReturnNonNull {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    nativeApi.log().error("some error message");
    return nativeApi.factory().string("abc");
  }
}
