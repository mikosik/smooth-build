package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class EmptyStringArray {
  @SmoothFunction("emptyStringArray")
  public static Array emptyStringArray(NativeApi nativeApi) {
    return nativeApi.create().arrayBuilder(nativeApi.types().string()).build();
  }
}
