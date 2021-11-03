package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.plugin.NativeApi;

public class Concat {
  public static Array function(NativeApi nativeApi, Array first, Array second) {
    return nativeApi.factory()
        .arrayBuilder(first.type().element())
        .addAll(first.elements(Val.class))
        .addAll(second.elements(Val.class))
        .build();
  }
}
