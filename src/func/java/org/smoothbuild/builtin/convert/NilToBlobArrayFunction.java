package org.smoothbuild.builtin.convert;

import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;

import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.SNothing;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class NilToBlobArrayFunction {

  public interface Parameters {
    @Required
    public Array<SNothing> input();
  }

  @SmoothFunction
  public static Array<Blob> nilToBlobArray(NativeApi nativeApi, Parameters params) {
    return nativeApi.arrayBuilder(BLOB_ARRAY).build();
  }
}
