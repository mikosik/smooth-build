package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.Array;

public class EmptyStringArray {
  @SmoothFunction("emptyStringArray")
  public static Array emptyStringArray(NativeApi nativeApi) {
    return nativeApi.factory().arrayBuilder(nativeApi.factory().stringSpec()).build();
  }
}
