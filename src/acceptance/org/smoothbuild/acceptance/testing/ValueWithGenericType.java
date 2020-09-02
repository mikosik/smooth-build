package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class ValueWithGenericType {
  @SmoothFunction("valueWithGenericType")
  public static Record valueWithGenericType(NativeApi nativeApi) {
    return nativeApi.factory().string("abc");
  }
}