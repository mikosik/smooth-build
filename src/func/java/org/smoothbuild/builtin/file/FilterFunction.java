package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.match.PathMatcher.pathMatcher;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;

import org.smoothbuild.builtin.BuiltinSmoothModule;
import org.smoothbuild.builtin.file.err.IllegalPathPatternError;
import org.smoothbuild.builtin.file.match.IllegalPathPatternException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.task.exec.NativeApiImpl;

import com.google.common.base.Predicate;

public class FilterFunction {
  public static SArray<SFile> execute(NativeApiImpl nativeApi,
      BuiltinSmoothModule.FilterParameters params) {
    return new Worker(nativeApi, params).execute();
  }

  private static class Worker {
    private final NativeApi nativeApi;
    private final BuiltinSmoothModule.FilterParameters params;

    public Worker(NativeApi nativeApi, BuiltinSmoothModule.FilterParameters params) {
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
