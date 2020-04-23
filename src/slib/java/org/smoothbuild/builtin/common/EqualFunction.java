package org.smoothbuild.builtin.common;

import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class EqualFunction {
  @SmoothFunction("equal")
  public static Bool equalFunction(NativeApi nativeApi, SObject first, SObject second) {
    return nativeApi.factory().bool(first.hash().equals(second.hash()));
  }
}