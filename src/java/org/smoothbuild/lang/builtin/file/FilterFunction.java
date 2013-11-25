package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.io.fs.match.PathMatcher.pathMatcher;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.match.IllegalPathPatternException;
import org.smoothbuild.lang.builtin.file.err.IllegalPathPatternError;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.PluginApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.task.exec.PluginApiImpl;

import com.google.common.base.Predicate;

public class FilterFunction {
  public interface Parameters {
    @Required
    public SArray<SFile> files();

    @Required
    public SString include();
  }

  @SmoothFunction(name = "filter")
  public static SArray<SFile> execute(PluginApiImpl pluginApi, Parameters params) {
    return new Worker(pluginApi, params).execute();
  }

  private static class Worker {
    private final PluginApi pluginApi;
    private final Parameters params;

    public Worker(PluginApi pluginApi, Parameters params) {
      this.pluginApi = pluginApi;
      this.params = params;
    }

    public SArray<SFile> execute() {
      Predicate<Path> filter = createFilter();
      ArrayBuilder<SFile> builder = pluginApi.arrayBuilder(FILE_ARRAY);

      for (SFile file : params.files()) {
        if (filter.apply(file.path())) {
          builder.add(file);
        }
      }

      return builder.build();
    }

    private Predicate<Path> createFilter() {
      try {
        return pathMatcher(params.include().value());
      } catch (IllegalPathPatternException e) {
        throw new ErrorMessageException(new IllegalPathPatternError("include", e.getMessage()));
      }
    }
  }
}
