package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.ArrayBuilder;
import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.db.object.type.val.ArrayTypeO;
import org.smoothbuild.plugin.NativeApi;

public class Flatten {
  public static Array function(NativeApi nativeApi, Array array) {
    TypeV resultArrayElemType = ((ArrayTypeO) array.type().element()).element();
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(resultArrayElemType);
    for (Array innerArray : array.elements(Array.class)) {
      builder.addAll(innerArray.elements(Val.class));
    }
    return builder.build();
  }
}
