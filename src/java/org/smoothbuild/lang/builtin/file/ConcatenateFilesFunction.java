package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.lang.plugin.PluginApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;

public class ConcatenateFilesFunction {

  public interface Parameters {
    @Required
    public SArray<SFile> files();

    @Required
    public SArray<SFile> with();
  }

  @SmoothFunction(name = "concatenateFiles")
  public static SArray<SFile> execute(PluginApi pluginApi, Parameters params) {
    return new Worker(pluginApi, params).execute();
  }

  public static class Worker {
    private final PluginApi pluginApi;
    private final Parameters params;

    public Worker(PluginApi pluginApi, Parameters params) {
      this.pluginApi = pluginApi;
      this.params = params;
    }

    public SArray<SFile> execute() {
      ArrayBuilder<SFile> builder = pluginApi.arrayBuilder(FILE_ARRAY);

      for (SFile file : params.files()) {
        builder.add(file);
      }
      for (SFile file : params.with()) {
        builder.add(file);
      }

      return builder.build();
    }
  }
}
