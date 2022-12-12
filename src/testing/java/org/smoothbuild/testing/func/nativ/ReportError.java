package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.value.StringB;
import org.smoothbuild.bytecode.expr.value.TupleB;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.plugin.NativeApi;

public class ReportError {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    StringB message = (StringB) args.get(0);
    nativeApi.log().error(message.toJ());
    return null;
  }
}
