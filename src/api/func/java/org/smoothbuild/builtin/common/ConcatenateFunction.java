package org.smoothbuild.builtin.common;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.Value;

public class ConcatenateFunction {
  @SmoothFunction
  public static Array concatenate(NativeApi nativeApi, Array array1, Array array2) {
    ArrayBuilder builder = nativeApi.create().arrayBuilder(array1.type().elemType());
    builder.addAll(array1.asIterable(Value.class));
    builder.addAll(array2.asIterable(Value.class));
    return builder.build();
  }
}
