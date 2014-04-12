package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.io.fs.match.PathMatcher.pathMatcher;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.match.IllegalPathPatternException;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.builtin.file.err.IllegalPathPatternError;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.task.exec.NativeApiImpl;

import com.google.common.base.Predicate;

public class FilterFunction {
  public interface Parameters {
    @Required
    public SArray<SFile> files();

    @Required
    public SString include();
  }

  @SmoothFunction(name = "filter")
  public static SArray<SFile> execute(NativeApiImpl nativeApi, Parameters params) {
    return new Worker(nativeApi, params).execute();
  }

  private static class Worker {
    private final NativeApi nativeApi;
    private final Parameters params;

    public Worker(NativeApi nativeApi, Parameters params) {
      this.nativeApi = nativeApi;
      this.params = params;
    }

    public SArray<SFile> execute() {
      Predicate<Path> filter = createFilter();
      ArrayBuilder<SFile> builder = nativeApi.arrayBuilder(FILE_ARRAY);

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
        throw new IllegalPathPatternError("include", e.getMessage());
      }
    }
  }
}
