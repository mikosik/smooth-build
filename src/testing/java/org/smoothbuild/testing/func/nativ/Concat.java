package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.InstB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class Concat {
  public static InstB func(NativeApi nativeApi, TupleB args) {
    ArrayB first = (ArrayB) args.get(0);
    ArrayB second = (ArrayB) args.get(1);

    var elemT = first.evalT().elem();
    return nativeApi.factory()
        .arrayBuilderWithElems(elemT)
        .addAll(first.elems(InstB.class))
        .addAll(second.elems(InstB.class))
        .build();
  }
}
