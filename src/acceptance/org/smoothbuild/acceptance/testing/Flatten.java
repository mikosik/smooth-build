package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.ArrayHBuilder;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.type.val.ArrayTH;
import org.smoothbuild.plugin.NativeApi;

public class Flatten {
  public static ArrayH func(NativeApi nativeApi, ArrayH array) {
    ArrayHBuilder builder = nativeApi.factory().arrayBuilder((ArrayTH) array.cat().elem());
    for (ArrayH innerArray : array.elems(ArrayH.class)) {
      builder.addAll(innerArray.elems(ValH.class));
    }
    return builder.build();
  }
}
