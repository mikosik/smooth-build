package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Value;

public class Append {
  @SmoothFunction
  public static Array testAppend(NativeApi nativeApi, Array array, Value element) {
    return nativeApi.create()
        .arrayBuilder(array.type().elemType())
        .addAll(array.asIterable(Value.class))
        .add(element)
        .build();
  }
}
