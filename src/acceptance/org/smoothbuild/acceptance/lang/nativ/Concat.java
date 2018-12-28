package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Value;

public class Concat {
  @SmoothFunction("testConcat")
  public static Array testConcat(NativeApi nativeApi, Array first, Array second) {
    return nativeApi.create()
        .arrayBuilder(first.type().elemType())
        .addAll(first.asIterable(Value.class))
        .addAll(second.asIterable(Value.class))
        .build();
  }
}
