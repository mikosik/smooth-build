package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArrayBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.type.base.BArrayType;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class Flatten {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    BArray array = (BArray) args.get(0);

    BArrayBuilder builder =
        nativeApi.factory().arrayBuilder((BArrayType) array.evaluationType().elem());
    for (BArray innerArray : array.elements(BArray.class)) {
      builder.addAll(innerArray.elements(BValue.class));
    }
    return builder.build();
  }
}
