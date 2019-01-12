package org.smoothbuild.builtin.bool;

import java.io.IOException;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Bool;

public class OrFunction  {
  @SmoothFunction("or")
  public static Bool or(NativeApi nativeApi, Bool first, Bool second)
      throws IOException {
    return nativeApi.create().bool(first.data() || second.data());
  }
}
