package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SFile;

public class NilToFileArrayFunction {
  @SmoothFunction
  public static Array<SFile> nilToFileArray( //
      NativeApi nativeApi, //
      @Required @Name("input") Array<Nothing> input) {
    return nativeApi.arrayBuilder(SFile.class).build();
  }
}
