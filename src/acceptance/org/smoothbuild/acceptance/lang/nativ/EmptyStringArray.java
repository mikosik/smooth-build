package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;

public class EmptyStringArray {
  @SmoothFunction
  public static Array emptyStringArray(NativeApi nativeApi) {
    return nativeApi.create().arrayBuilder(nativeApi.types().string()).build();
  }
}
