package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.plugin.NativeApi;

public class Concat {
  public static ArrayH function(NativeApi nativeApi, ArrayH first, ArrayH second) {
    return nativeApi.factory()
        .arrayBuilder(first.type().element())
        .addAll(first.elements(ValueH.class))
        .addAll(second.elements(ValueH.class))
        .build();
  }
}
