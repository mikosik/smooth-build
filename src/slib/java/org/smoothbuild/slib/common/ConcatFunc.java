package org.smoothbuild.slib.common;

import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.plugin.NativeApi;

public class ConcatFunc {
  public static ArrayH func(NativeApi nativeApi, ArrayH array1, ArrayH array2) {
    var factory = nativeApi.factory();
    var elemType = ((ArrayTypeH) factory.typing().mergeUp(array1.spec(), array2.spec())).elem();
    return factory
        .arrayBuilderWithElems(elemType)
        .addAll(array1.elems(ValH.class))
        .addAll(array2.elems(ValH.class))
        .build();
  }
}
