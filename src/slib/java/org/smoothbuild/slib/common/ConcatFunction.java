package org.smoothbuild.slib.common;

import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.ArrayBuilder;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ConcatFunction {
  @SmoothFunction("concat")
  public static Array concat(NativeApi nativeApi, Array array1, Array array2) {
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(array1.type().elemType());
    builder.addAll(array1.asIterable(SObject.class));
    builder.addAll(array2.asIterable(SObject.class));
    return builder.build();
  }
}
