package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class Append {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    ArrayB array = (ArrayB) args.get(0);
    ValueB elem = args.get(1);
    return nativeApi.factory()
        .arrayBuilderWithElems(array.evalT().elem())
        .addAll(array.elems(ValueB.class))
        .add(elem)
        .build();
  }
}
