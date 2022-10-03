package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.InstB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class Append {
  public static InstB func(NativeApi nativeApi, TupleB args) {
    ArrayB array = (ArrayB) args.get(0);
    InstB elem = args.get(1);
    return nativeApi.factory()
        .arrayBuilderWithElems(array.type().elem())
        .addAll(array.elems(InstB.class))
        .add(elem)
        .build();
  }
}
