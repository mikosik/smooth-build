package org.smoothbuild.slib.testing;

import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

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
