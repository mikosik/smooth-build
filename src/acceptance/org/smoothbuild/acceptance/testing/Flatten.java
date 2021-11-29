package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.ArrayHBuilder;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.plugin.NativeApi;

public class Flatten {
  public static ArrayH function(NativeApi nativeApi, ArrayH array) {
    TypeH resultArrayElemType = ((ArrayTypeH) array.spec().elem()).elem();
    ArrayHBuilder builder = nativeApi.factory().arrayBuilder(resultArrayElemType);
    for (ArrayH innerArray : array.elems(ArrayH.class)) {
      builder.addAll(innerArray.elems(ValueH.class));
    }
    return builder.build();
  }
}
