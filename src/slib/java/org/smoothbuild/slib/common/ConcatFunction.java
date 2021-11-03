package org.smoothbuild.slib.common;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.ArrayBuilder;
import org.smoothbuild.plugin.NativeApi;

public class ConcatFunction {
  public static Array function(NativeApi nativeApi, Array array1, Array array2) {
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(array1.type().element());
    builder.addAll(array1.elements(Val.class));
    builder.addAll(array2.elements(Val.class));
    return builder.build();
  }
}
