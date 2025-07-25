package org.smoothbuild.stdlib.array;

import java.math.BigInteger;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class SizeFunc {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    var array = (BArray) args.get(0);
    long size = array.size();
    return nativeApi.factory().int_(BigInteger.valueOf(size));
  }
}
