package org.smoothbuild.slib.common;

import org.smoothbuild.db.object.obj.val.ArrayB;
import org.smoothbuild.db.object.obj.val.ValB;
import org.smoothbuild.db.object.type.val.ArrayTB;
import org.smoothbuild.plugin.NativeApi;

public class ConcatFunc {
  public static ArrayB func(NativeApi nativeApi, ArrayB array1, ArrayB array2) {
    var factory = nativeApi.factory();
    var elemT = ((ArrayTB) factory.typing().mergeUp(array1.cat(), array2.cat())).elem();
    return factory
        .arrayBuilderWithElems(elemT)
        .addAll(array1.elems(ValB.class))
        .addAll(array2.elems(ValB.class))
        .build();
  }
}
