package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.plugin.NativeApi;

public class Append {
  public static ArrayH function(NativeApi nativeApi, ArrayH array, ValueH element) {
    return nativeApi.factory()
        .arrayBuilder(array.type().element())
        .addAll(array.elements(ValueH.class))
        .add(element)
        .build();
  }
}
