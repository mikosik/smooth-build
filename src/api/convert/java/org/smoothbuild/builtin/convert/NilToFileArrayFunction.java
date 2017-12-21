package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;

public class NilToFileArrayFunction {
  @SmoothFunction
  public static Array nilToFileArray(NativeApi nativeApi, Array nil) {
    return nativeApi.create().arrayBuilder(nativeApi.types().file()).build();
  }
}
