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

import com.google.common.base.Predicate;

public class FilterFunction {
  public static SArray<SFile> execute(NativeApi nativeApi,
      BuiltinSmoothModule.FilterParameters params) {
    Predicate<Path> filter = createFilter(params.include().value());
    ArrayBuilder<SFile> builder = nativeApi.arrayBuilder(FILE_ARRAY);

    for (SFile file : params.files()) {
      if (filter.apply(file.path())) {
        builder.add(file);
      }
    }

    return builder.build();
  }

  private static Predicate<Path> createFilter(String pattern) {
    try {
      return pathMatcher(pattern);
    } catch (IllegalPathPatternException e) {
      throw new IllegalPathPatternError("include", e.getMessage());
    }
  }
}
