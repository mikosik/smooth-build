package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.Val;
import org.smoothbuild.plugin.NativeApi;

public class Append {
  public static Array function(NativeApi nativeApi, Array array, Val element) {
    return nativeApi.factory()
        .arrayBuilder(array.spec().elemSpec())
        .addAll(array.elements(Val.class))
        .add(element)
        .build();
  }
}
