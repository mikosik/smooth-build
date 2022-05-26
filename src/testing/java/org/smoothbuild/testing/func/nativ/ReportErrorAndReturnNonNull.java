package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.cnst.StringB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class ReportErrorAndReturnNonNull {
  public static StringB func(NativeApi nativeApi, TupleB args) {
    nativeApi.log().error("some error message");
    return nativeApi.factory().string("abc");
  }
}
