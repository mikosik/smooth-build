package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.Nothing;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class NilToFileArrayFunction {

  public interface Parameters {
    @Required
    public Array<Nothing> input();
  }

  @SmoothFunction
  public static Array<SFile> nilToFileArray(NativeApi nativeApi, Parameters params) {
    return nativeApi.arrayBuilder(SFile.class).build();
  }
}
