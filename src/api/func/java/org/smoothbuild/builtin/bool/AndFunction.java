package org.smoothbuild.builtin.bool;

import java.io.IOException;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Bool;
import org.smoothbuild.lang.value.Value;

public class AndFunction {
  @SmoothFunction("and")
  public static Bool and(NativeApi nativeApi, Bool first, Bool second)
      throws IOException {
    return nativeApi.create().bool(first.data() && second.data());
  }
}