package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.value.StringB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class ReportWarning {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    StringB message = (StringB) args.get(0);
    nativeApi.log().warning(message.toJ());
    return message;
  }
}
