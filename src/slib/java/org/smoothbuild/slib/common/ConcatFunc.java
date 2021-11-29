package org.smoothbuild.slib.common;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.ArrayHBuilder;
import org.smoothbuild.plugin.NativeApi;

public class ConcatFunc {
  public static ArrayH func(NativeApi nativeApi, ArrayH array1, ArrayH array2) {
    ArrayHBuilder builder = nativeApi.factory().arrayBuilder(array1.spec().elem());
    builder.addAll(array1.elems(ValueH.class));
    builder.addAll(array2.elems(ValueH.class));
    return builder.build();
  }
}
