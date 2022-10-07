package org.smoothbuild.slib.array;

import java.math.BigInteger;

import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class SizeFunc {
  public static InstB func(NativeApi nativeApi, TupleB args) {
    var arrayB = (ArrayB) args.get(0);
    long size = arrayB.size();
    return nativeApi.factory().int_(BigInteger.valueOf(size));
  }
}
