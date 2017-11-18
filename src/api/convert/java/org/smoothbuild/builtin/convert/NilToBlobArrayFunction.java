package org.smoothbuild.builtin.convert;

import static org.smoothbuild.lang.type.Types.BLOB;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;

public class NilToBlobArrayFunction {
  @SmoothFunction
  public static Array nilToBlobArray(NativeApi nativeApi, Array nil) {
    return nativeApi.create().arrayBuilder(BLOB).build();
  }
}
