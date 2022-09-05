package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class Concat {
  public static ValB func(NativeApi nativeApi, TupleB args) {
    ArrayB first = (ArrayB) args.get(0);
    ArrayB second = (ArrayB) args.get(1);

    var elemT = first.cat().elem();
    return nativeApi.factory()
        .arrayBuilderWithElems(elemT)
        .addAll(first.elems(ValB.class))
        .addAll(second.elems(ValB.class))
        .build();
  }
}
