package org.smoothbuild.lang.builtin.convert;

import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;

import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SNothing;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class NilToBlobArrayFunction {

  public interface Parameters {
    @Required
    public SArray<SNothing> input();
  }

  @SmoothFunction(name = "nilToBlobArray")
  public static SArray<SBlob> execute(NativeApi nativeApi, Parameters params) {
    return nativeApi.arrayBuilder(BLOB_ARRAY).build();
  }
}
