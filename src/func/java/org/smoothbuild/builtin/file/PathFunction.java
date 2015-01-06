package org.smoothbuild.builtin.file;

import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;

public class PathFunction {
  @SmoothFunction
  public static SString path( //
      NativeApi nativeApi, //
      @Required @Name("file") SFile file) {
    return nativeApi.string(file.path().value());
  }
}
