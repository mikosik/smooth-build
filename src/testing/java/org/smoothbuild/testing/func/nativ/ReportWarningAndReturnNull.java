package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class ReportWarningAndReturnNull {
  public static InstB func(NativeApi nativeApi, TupleB args) {
    nativeApi.log().warning("some warning message");
    return null;
  }
}
