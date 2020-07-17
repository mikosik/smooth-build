package org.smoothbuild.slib.common;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.Bool;
import org.smoothbuild.record.base.SObject;

public class EqualFunction {
  @SmoothFunction("equal")
  public static Bool equalFunction(NativeApi nativeApi, SObject first, SObject second) {
    return nativeApi.factory().bool(first.hash().equals(second.hash()));
  }
}
