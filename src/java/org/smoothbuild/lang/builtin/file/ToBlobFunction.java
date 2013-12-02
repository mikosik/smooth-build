package org.smoothbuild.lang.builtin.file;

import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.task.exec.PluginApiImpl;

public class ToBlobFunction {
  public interface Parameters {
    @Required
    public SFile file();
  }

  @SmoothFunction(name = "toBlob")
  public static SBlob execute(PluginApiImpl pluginApi, Parameters params) {
    return params.file().content();
  }
}
