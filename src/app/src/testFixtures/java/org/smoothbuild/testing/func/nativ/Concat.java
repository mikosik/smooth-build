package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class Concat {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    ArrayB first = (ArrayB) args.get(0);
    ArrayB second = (ArrayB) args.get(1);

    var elementT = first.evaluationT().elem();
    return nativeApi.factory()
        .arrayBuilderWithElems(elementT)
        .addAll(first.elems(ValueB.class))
        .addAll(second.elems(ValueB.class))
        .build();
  }
}
