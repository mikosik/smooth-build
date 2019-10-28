package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class GenericResult {
  @SmoothFunction("genericResult")
  public static SObject genericResult(NativeApi nativeApi, Array array) {
    return nativeApi.create().string("abc");
  }
}
