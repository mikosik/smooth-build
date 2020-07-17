package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.SObject;

public class Concat {
  @SmoothFunction("testConcat")
  public static Array testConcat(NativeApi nativeApi, Array first, Array second) {
    return nativeApi.factory()
        .arrayBuilder(first.type().elemType())
        .addAll(first.asIterable(SObject.class))
        .addAll(second.asIterable(SObject.class))
        .build();
  }
}
