package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.plugin.NativeApi;

public class EmptyStringArray {
  public static ArrayH func(NativeApi nativeApi) {
    return nativeApi.factory().arrayBuilder(nativeApi.factory().stringT()).build();
  }
}
