package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.ArrayB;
import org.smoothbuild.plugin.NativeApi;

public class EmptyStringArray {
  public static ArrayB func(NativeApi nativeApi) {
    return nativeApi.factory().arrayBuilderWithElems(nativeApi.factory().stringT()).build();
  }
}
