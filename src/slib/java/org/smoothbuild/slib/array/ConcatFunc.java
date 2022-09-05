package org.smoothbuild.slib.array;

import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class ConcatFunc {
  public static ValB func(NativeApi nativeApi, TupleB args) {
    ArrayB array1 = (ArrayB) args.get(0);
    ArrayB array2 = (ArrayB) args.get(1);
    var factory = nativeApi.factory();
    var elemT = array1.cat().elem();
    return factory
        .arrayBuilderWithElems(elemT)
        .addAll(array1.elems(ValB.class))
        .addAll(array2.elems(ValB.class))
        .build();
  }
}
