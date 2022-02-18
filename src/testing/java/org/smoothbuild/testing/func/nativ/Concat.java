package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.plugin.NativeApi;

public class Concat {
  public static ArrayB func(NativeApi nativeApi, TupleB args) {
    ArrayB first = (ArrayB) args.get(0);
    ArrayB second = (ArrayB) args.get(1);

    var elemT = ((ArrayTB) nativeApi.typing().mergeUp(first.cat(), second.cat())).elem();
    return nativeApi.factory()
        .arrayBuilderWithElems(elemT)
        .addAll(first.elems(ValB.class))
        .addAll(second.elems(ValB.class))
        .build();
  }
}
