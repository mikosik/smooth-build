package org.smoothbuild.builtin.common;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Bool;
import org.smoothbuild.lang.value.Value;

public class EqualFunction {
  @SmoothFunction("equal")
  public static Bool equalFunction(NativeApi nativeApi, Value first, Value second) {
    return nativeApi.create().bool(first.hash().equals(second.hash()));
  }
}