package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArrayBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.bytecode.type.value.BArrayType;
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
