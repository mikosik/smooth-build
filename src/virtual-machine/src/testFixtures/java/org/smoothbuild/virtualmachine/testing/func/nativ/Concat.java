package org.smoothbuild.virtualmachine.testing.func.nativ;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class Concat {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    ArrayB first = (ArrayB) args.get(0);
    ArrayB second = (ArrayB) args.get(1);

    var elementT = first.evaluationType().elem();
    return nativeApi
        .factory()
        .arrayBuilderWithElements(elementT)
        .addAll(first.elements(ValueB.class))
        .addAll(second.elements(ValueB.class))
        .build();
  }
}
