package org.smoothbuild.lang.builtin.file;

import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.task.exec.NativeApiImpl;

public class PathFunction {
  public interface Parameters {
    @Required
    public SFile file();
  }

  @SmoothFunction(name = "path")
  public static SString execute(NativeApiImpl nativeApi, Parameters params) {
    return nativeApi.string(params.file().path().value());
  }
}
