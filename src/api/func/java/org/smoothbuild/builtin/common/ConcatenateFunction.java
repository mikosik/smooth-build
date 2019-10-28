package org.smoothbuild.builtin.common;

import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.ArrayBuilder;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ConcatenateFunction {
  @SmoothFunction("concatenate")
  public static Array concatenate(NativeApi nativeApi, Array array1, Array array2) {
    ArrayBuilder builder = nativeApi.create().arrayBuilder(array1.type().elemType());
    builder.addAll(array1.asIterable(SObject.class));
    builder.addAll(array2.asIterable(SObject.class));
    return builder.build();
  }
}
