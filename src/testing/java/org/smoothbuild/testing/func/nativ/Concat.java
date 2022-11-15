package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.expr.inst.ValueB;
import org.smoothbuild.plugin.NativeApi;

public class Concat {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    ArrayB first = (ArrayB) args.get(0);
    ArrayB second = (ArrayB) args.get(1);

    var elemT = first.evalT().elem();
    return nativeApi.factory()
        .arrayBuilderWithElems(elemT)
        .addAll(first.elems(ValueB.class))
        .addAll(second.elems(ValueB.class))
        .build();
  }
}
