package org.smoothbuild.builtin.file;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;

public class PathFunction {

  public interface PathParameters {
    @Required
    public SFile file();
  }

  @SmoothFunction
  public static SString path(NativeApi nativeApi, PathParameters params) {
    return nativeApi.string(params.file().path().value());
  }
}
