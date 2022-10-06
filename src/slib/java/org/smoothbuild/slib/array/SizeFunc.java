package org.smoothbuild.slib.array;

import java.math.BigInteger;

import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.InstB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class SizeFunc {
  public static InstB func(NativeApi nativeApi, TupleB args) {
    var arrayB = (ArrayB) args.get(0);
    long size = arrayB.size();
    return nativeApi.factory().int_(BigInteger.valueOf(size));
  }
}
