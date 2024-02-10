package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.value.StringB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class ReportInfo {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    StringB message = (StringB) args.get(0);
    nativeApi.log().info(message.toJ());
    return message;
  }
}
