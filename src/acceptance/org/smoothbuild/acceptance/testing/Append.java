package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.plugin.NativeApi;

public class Append {
  public static ArrayH func(NativeApi nativeApi, ArrayH array, ValH elem) {
    var factory = nativeApi.factory();
    return factory
        .arrayBuilderWithElems(factory.typing().mergeUp(array.cat().elem(), elem.cat()))
        .addAll(array.elems(ValH.class))
        .add(elem)
        .build();
  }
}
