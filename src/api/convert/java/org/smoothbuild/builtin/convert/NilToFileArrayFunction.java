package org.smoothbuild.builtin.convert;

import static org.smoothbuild.lang.type.Types.FILE;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;

public class NilToFileArrayFunction {
  @SmoothFunction
  public static Array nilToFileArray(NativeApi nativeApi, Array nil) {
    return nativeApi.create().arrayBuilder(FILE).build();
  }
}
