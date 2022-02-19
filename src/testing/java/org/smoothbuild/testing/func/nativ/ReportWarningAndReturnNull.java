package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class ReportWarningAndReturnNull {
  public static StringB func(NativeApi nativeApi, TupleB args) {
    nativeApi.log().warning("some warning message");
    return null;
  }
}