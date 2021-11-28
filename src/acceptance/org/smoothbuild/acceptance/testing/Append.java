package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.plugin.NativeApi;

public class Append {
  public static ArrayH function(NativeApi nativeApi, ArrayH array, ValueH elem) {
    var factory = nativeApi.factory();
    return factory
        .arrayBuilder(factory.typing().mergeUp(array.type().elem(), elem.type()))
        .addAll(array.elems(ValueH.class))
        .add(elem)
        .build();
  }
}
