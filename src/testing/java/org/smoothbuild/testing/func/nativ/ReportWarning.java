package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.value.StringB;
import org.smoothbuild.bytecode.expr.value.TupleB;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.plugin.NativeApi;

public class ReportWarning {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    StringB message = (StringB) args.get(0);
    nativeApi.log().warning(message.toJ());
    return message;
  }
}
