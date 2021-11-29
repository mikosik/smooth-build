package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.plugin.NativeApi;

public class Append {
  public static ArrayH func(NativeApi nativeApi, ArrayH array, ValueH elem) {
    var factory = nativeApi.factory();
    return factory
        .arrayBuilder(factory.typing().mergeUp(array.spec().elem(), elem.spec()))
        .addAll(array.elems(ValueH.class))
        .add(elem)
        .build();
  }
}
