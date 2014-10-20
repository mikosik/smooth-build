package org.smoothbuild.builtin.file;

import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class PathFunction {

  public interface PathParameters {
    @Required
    public SFile file();
  }

  @SmoothFunction(name = "path")
  public static SString path(NativeApi nativeApi, PathParameters params) {
    return nativeApi.string(params.file().path().value());
  }
}
