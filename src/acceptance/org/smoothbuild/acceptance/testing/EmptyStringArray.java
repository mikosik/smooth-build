package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.plugin.NativeApi;

public class EmptyStringArray {
  public static Array function(NativeApi nativeApi) {
    return nativeApi.factory().arrayBuilder(nativeApi.factory().stringSpec()).build();
  }
}
