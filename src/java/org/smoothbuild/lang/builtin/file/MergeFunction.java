package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.builtin.file.err.DuplicateMergedPathError;
import org.smoothbuild.lang.plugin.PluginApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.util.DuplicatesDetector;

public class MergeFunction {

  public interface Parameters {
    @Required
    public SArray<SFile> files();

    @Required
    public SArray<SFile> with();
  }

  @SmoothFunction(name = "merge")
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
      DuplicatesDetector<Path> duplicatesDetector = new DuplicatesDetector<Path>();
      ArrayBuilder<SFile> builder = pluginApi.arrayBuilder(FILE_ARRAY);

      for (SFile file : params.files()) {
        addFile(file, builder, duplicatesDetector);
      }
      for (SFile file : params.with()) {
        addFile(file, builder, duplicatesDetector);
      }

      return builder.build();
    }

    private void addFile(SFile file, ArrayBuilder<SFile> builder,
        DuplicatesDetector<Path> duplicatesDetector) {
      Path path = file.path();
      if (duplicatesDetector.add(path)) {
        pluginApi.report(new DuplicateMergedPathError(path));
      } else {
        builder.add(file);
      }
    }
  }
}
