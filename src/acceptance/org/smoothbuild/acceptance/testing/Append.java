package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.plugin.NativeApi;

public class Append {
  public static Array function(NativeApi nativeApi, Array array, Obj element) {
    return nativeApi.factory()
        .arrayBuilder(array.spec().elemSpec())
        .addAll(array.asIterable(Obj.class))
        .add(element)
        .build();
  }
}
