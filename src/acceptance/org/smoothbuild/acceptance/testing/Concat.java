package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class Concat {
  @SmoothFunction("testConcat")
  public static Array testConcat(NativeApi nativeApi, Array first, Array second) {
    return nativeApi.create()
        .arrayBuilder(first.type().elemType())
        .addAll(first.asIterable(SObject.class))
        .addAll(second.asIterable(SObject.class))
        .build();
  }
}
