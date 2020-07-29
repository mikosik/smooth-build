package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class GenericResult {
  @SmoothFunction("genericResult")
  public static Record genericResult(NativeApi nativeApi, Array array) {
    return nativeApi.factory().string("abc");
  }
}
