package org.smoothbuild.slib.testing;

import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class EmptyStringArray {
  @SmoothFunction("emptyStringArray")
  public static Array emptyStringArray(NativeApi nativeApi) {
    return nativeApi.factory().arrayBuilder(nativeApi.factory().stringType()).build();
  }
}
