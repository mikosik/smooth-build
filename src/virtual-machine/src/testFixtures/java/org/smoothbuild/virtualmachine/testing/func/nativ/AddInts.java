package org.smoothbuild.virtualmachine.testing.func.nativ;

import java.math.BigInteger;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class AddInts {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    BInt first = (BInt) args.get(0);
    BInt second = (BInt) args.get(1);

    BigInteger result = first.toJavaBigInteger().add(second.toJavaBigInteger());
    return nativeApi.factory().int_(result);
  }
}
