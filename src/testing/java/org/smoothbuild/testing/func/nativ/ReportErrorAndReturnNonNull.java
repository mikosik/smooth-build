package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class ReportErrorAndReturnNonNull {
  public static InstB func(NativeApi nativeApi, TupleB args) {
    nativeApi.log().error("some error message");
    return nativeApi.factory().string("abc");
  }
}
