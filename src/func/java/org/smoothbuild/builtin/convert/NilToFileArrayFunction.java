package org.smoothbuild.builtin.convert;

import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;

import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SNothing;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class NilToFileArrayFunction {

  public interface Parameters {
    @Required
    public SArray<SNothing> input();
  }

  @SmoothFunction
  public static SArray<SFile> nilToFileArray(NativeApi nativeApi, Parameters params) {
    return nativeApi.arrayBuilder(FILE_ARRAY).build();
  }
}
