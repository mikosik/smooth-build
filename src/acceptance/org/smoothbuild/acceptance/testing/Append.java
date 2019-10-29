package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class Append {
  @SmoothFunction("testAppend")
  public static Array testAppend(NativeApi nativeApi, Array array, SObject element) {
    return nativeApi.factory()
        .arrayBuilder(array.type().elemType())
        .addAll(array.asIterable(SObject.class))
        .add(element)
        .build();
  }
}
