package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.StringB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class ReportInfo {
  public static InstB func(NativeApi nativeApi, TupleB args) {
    StringB message = (StringB) args.get(0);
    nativeApi.log().info(message.toJ());
    return message;
  }
}
