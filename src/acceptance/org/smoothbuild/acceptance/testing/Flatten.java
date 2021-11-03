package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.ArrayBuilder;
import org.smoothbuild.db.object.type.base.ValType;
import org.smoothbuild.db.object.type.val.ArrayOType;
import org.smoothbuild.plugin.NativeApi;

public class Flatten {
  public static Array function(NativeApi nativeApi, Array array) {
    ValType resultArrayElemType = ((ArrayOType) array.type().element()).element();
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(resultArrayElemType);
    for (Array innerArray : array.elements(Array.class)) {
      builder.addAll(innerArray.elements(Val.class));
    }
    return builder.build();
  }
}
