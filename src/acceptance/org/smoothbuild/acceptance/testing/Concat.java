package org.smoothbuild.acceptance.testing;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.plugin.NativeApi;

public class Concat {
  public static ArrayB func(NativeApi nativeApi, ArrayB first, ArrayB second) {
    var factory = nativeApi.factory();
    var elemT = ((ArrayTB) factory.typing().mergeUp(first.cat(), second.cat())).elem();
    return factory
        .arrayBuilderWithElems(elemT)
        .addAll(first.elems(ValB.class))
        .addAll(second.elems(ValB.class))
        .build();
  }
}
