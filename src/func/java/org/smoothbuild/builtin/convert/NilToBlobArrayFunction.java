package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.Nothing;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class NilToBlobArrayFunction {

  public interface Parameters {
    @Required
    public Array<Nothing> input();
  }

  @SmoothFunction
  public static Array<Blob> nilToBlobArray(NativeApi nativeApi, Parameters params) {
    return nativeApi.arrayBuilder(Blob.class).build();
  }
}
