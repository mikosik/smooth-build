package org.smoothbuild.acceptance.testing;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class Append {
  public static ArrayB func(NativeApi nativeApi, ArrayB array, ValB elem) {
    var factory = nativeApi.factory();
    return factory
        .arrayBuilderWithElems(factory.typing().mergeUp(array.cat().elem(), elem.cat()))
        .addAll(array.elems(ValB.class))
        .add(elem)
        .build();
  }
}
