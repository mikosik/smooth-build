package org.smoothbuild.testing.nativefunc;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.plugin.NativeApi;

public class EmptyStringArray {
  public static ArrayB func(NativeApi nativeApi) {
    return nativeApi.factory().arrayBuilderWithElems(nativeApi.factory().stringT()).build();
  }
}
