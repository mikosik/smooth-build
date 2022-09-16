package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class Append {
  public static ValB func(NativeApi nativeApi, TupleB args) {
    ArrayB array = (ArrayB) args.get(0);
    ValB elem = args.get(1);
    return nativeApi.factory()
        .arrayBuilderWithElems(array.type().elem())
        .addAll(array.elems(ValB.class))
        .add(elem)
        .build();
  }
}
