package org.smoothbuild.lang.builtin.file;

import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.task.exec.PluginApiImpl;

public class PathFunction {
  public interface Parameters {
    @Required
    public SFile file();
  }

  @SmoothFunction(name = "path")
  public static SString execute(PluginApiImpl pluginApi, Parameters params) {
    return pluginApi.string(params.file().path().value());
  }
}
