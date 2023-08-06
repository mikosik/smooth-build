package org.smoothbuild.stdlib.array;

import java.math.BigInteger;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class SizeFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    var arrayB = (ArrayB) args.get(0);
    long size = arrayB.size();
    return nativeApi.factory().int_(BigInteger.valueOf(size));
  }
}
