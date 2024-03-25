package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class Concat {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    BArray first = (BArray) args.get(0);
    BArray second = (BArray) args.get(1);

    var elementT = first.evaluationType().elem();
    return nativeApi
        .factory()
        .arrayBuilderWithElements(elementT)
        .addAll(first.elements(BValue.class))
        .addAll(second.elements(BValue.class))
        .build();
  }
}
