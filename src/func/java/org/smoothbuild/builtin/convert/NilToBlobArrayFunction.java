package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunctionLegacy;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Nothing;

public class NilToBlobArrayFunction {

  public interface Parameters {
    @Required
    public Array<Nothing> input();
  }

  @SmoothFunctionLegacy
  public static Array<Blob> nilToBlobArray(NativeApi nativeApi, Parameters params) {
    return nativeApi.arrayBuilder(Blob.class).build();
  }
}
