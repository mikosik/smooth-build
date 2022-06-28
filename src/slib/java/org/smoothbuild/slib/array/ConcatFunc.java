package org.smoothbuild.slib.array;

import org.smoothbuild.bytecode.obj.cnst.ArrayB;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class ConcatFunc {
  public static ArrayB func(NativeApi nativeApi, TupleB args) {
    ArrayB array1 = (ArrayB) args.get(0);
    ArrayB array2 = (ArrayB) args.get(1);
    var factory = nativeApi.factory();
    var elemT = array1.cat().elem();
    return factory
        .arrayBuilderWithElems(elemT)
        .addAll(array1.elems(CnstB.class))
        .addAll(array2.elems(CnstB.class))
        .build();
  }
}
