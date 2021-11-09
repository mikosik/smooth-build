package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.ArrayHBuilder;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.plugin.NativeApi;

public class Flatten {
  public static ArrayH function(NativeApi nativeApi, ArrayH array) {
    TypeHV resultArrayElemType = ((ArrayTypeH) array.type().element()).element();
    ArrayHBuilder builder = nativeApi.factory().arrayBuilder(resultArrayElemType);
    for (ArrayH innerArray : array.elements(ArrayH.class)) {
      builder.addAll(innerArray.elements(ValueH.class));
    }
    return builder.build();
  }
}
