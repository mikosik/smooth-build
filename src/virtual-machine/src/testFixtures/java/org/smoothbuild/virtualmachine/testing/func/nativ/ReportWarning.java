package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ReportWarning {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    StringB message = (StringB) args.get(0);
    nativeApi.log().warning(message.toJavaString());
    return message;
  }
}
