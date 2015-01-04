package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.match.PathMatcher.pathMatcher;

import org.smoothbuild.builtin.file.err.IllegalPathPatternError;
import org.smoothbuild.builtin.file.match.IllegalPathPatternException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;

import com.google.common.base.Predicate;

public class FilterFunction {
  @SmoothFunction
  public static Array<SFile> filter( //
      NativeApi nativeApi, //
      @Required @Name("files") Array<SFile> files, //
      @Required @Name("include") SString include) {
    Predicate<Path> filter = createFilter(include.value());
    ArrayBuilder<SFile> builder = nativeApi.arrayBuilder(SFile.class);

    for (SFile file : files) {
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
