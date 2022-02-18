package org.smoothbuild.slib.array;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.plugin.NativeApi;

public class ConcatFunc {
  public static ArrayB func(NativeApi nativeApi, TupleB args) {
    ArrayB array1 = (ArrayB) args.get(0);
    ArrayB array2 = (ArrayB) args.get(1);
    var factory = nativeApi.factory();
    var elemT = ((ArrayTB) nativeApi.typing().mergeUp(array1.cat(), array2.cat())).elem();
    return factory
        .arrayBuilderWithElems(elemT)
        .addAll(array1.elems(ValB.class))
        .addAll(array2.elems(ValB.class))
        .build();
  }
}
