package org.smoothbuild.nativefunc;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class Append {
  public static ArrayB func(NativeApi nativeApi, ArrayB array, ValB elem) {
    return nativeApi.factory()
        .arrayBuilderWithElems(nativeApi.typing().mergeUp(array.cat().elem(), elem.cat()))
        .addAll(array.elems(ValB.class))
        .add(elem)
        .build();
  }
}
