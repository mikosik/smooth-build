package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.expr.inst.ValueB;
import org.smoothbuild.plugin.NativeApi;

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
