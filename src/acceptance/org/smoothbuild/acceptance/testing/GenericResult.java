package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.Record;

public class GenericResult {
  @SmoothFunction("genericResult")
  public static Record genericResult(NativeApi nativeApi, Array array) {
    return nativeApi.factory().string("abc");
  }
}
