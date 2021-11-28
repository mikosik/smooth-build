package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.plugin.NativeApi;

public class Concat {
  public static ArrayH function(NativeApi nativeApi, ArrayH first, ArrayH second) {
    var factory = nativeApi.factory();
    var elemType = ((ArrayTypeH) factory.typing().mergeUp(first.type(), second.type())).elem();
    return factory
        .arrayBuilder(elemType)
        .addAll(first.elems(ValueH.class))
        .addAll(second.elems(ValueH.class))
        .build();
  }
}
