package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.plugin.NativeApi;

public class Append {
  public static Array function(NativeApi nativeApi, Array array, Val element) {
    return nativeApi.factory()
        .arrayBuilder(array.spec().element())
        .addAll(array.elements(Val.class))
        .add(element)
        .build();
  }
}
