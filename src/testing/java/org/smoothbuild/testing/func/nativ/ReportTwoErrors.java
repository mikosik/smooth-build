package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class ReportTwoErrors {
  public static StringB func(NativeApi nativeApi, TupleB args) {
    nativeApi.log().error("first error");
    nativeApi.log().error("second error");
    return null;
  }
}
