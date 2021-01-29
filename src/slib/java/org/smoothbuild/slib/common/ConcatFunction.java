package org.smoothbuild.slib.common;

import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.ArrayBuilder;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.plugin.NativeApi;

public class ConcatFunction {
  public static Array function(NativeApi nativeApi, Array array1, Array array2) {
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(array1.spec().elemSpec());
    builder.addAll(array1.asIterable(Obj.class));
    builder.addAll(array2.asIterable(Obj.class));
    return builder.build();
  }
}
