package org.smoothbuild.testing.nativefunc;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.plugin.NativeApi;

public class Concat {
  public static ArrayB func(NativeApi nativeApi, ArrayB first, ArrayB second) {
    var elemT = ((ArrayTB) nativeApi.typing().mergeUp(first.cat(), second.cat())).elem();
    return nativeApi.factory()
        .arrayBuilderWithElems(elemT)
        .addAll(first.elems(ValB.class))
        .addAll(second.elems(ValB.class))
        .build();
  }
}
