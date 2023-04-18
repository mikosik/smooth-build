package org.smoothbuild.stdlib.array;

import java.math.BigInteger;

import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class SizeFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    var arrayB = (ArrayB) args.get(0);
    long size = arrayB.size();
    return nativeApi.factory().int_(BigInteger.valueOf(size));
  }
}
