package org.smoothbuild.lang.builtin.java;

import org.smoothbuild.lang.plugin.PluginApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;

public class UnjarFunction {
  public interface Parameters {
    @Required
    public SBlob blob();
  }

  @SmoothFunction(name = "unjar")
  public static SArray<SFile> execute(PluginApi pluginApi, Parameters params) {
    return new Unjarer(pluginApi).unjar(params.blob());
  }
}
