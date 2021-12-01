package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.plugin.NativeApi;

public class Concat {
  public static ArrayH func(NativeApi nativeApi, ArrayH first, ArrayH second) {
    var factory = nativeApi.factory();
    var elemType = ((ArrayTypeH) factory.typing().mergeUp(first.spec(), second.spec())).elem();
    return factory
        .arrayBuilderWithElems(elemType)
        .addAll(first.elems(ValH.class))
        .addAll(second.elems(ValH.class))
        .build();
  }
}
