package org.smoothbuild.slib.common;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.Bool;
import org.smoothbuild.record.base.Record;

public class EqualFunction {
  @SmoothFunction("equal")
  public static Bool equalFunction(NativeApi nativeApi, Record first, Record second) {
    return nativeApi.factory().bool(first.hash().equals(second.hash()));
  }
}
