package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.match.PathMatcher.pathMatcher;
import static org.smoothbuild.io.fs.base.Path.path;

import java.util.function.Predicate;

import org.smoothbuild.builtin.file.match.IllegalPathPatternException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;

public class FilterFunction {
  @SmoothFunction
  public static Array<SFile> filter(Container container, Array<SFile> files, SString include) {
    Predicate<Path> filter = createFilter(include.value());
    ArrayBuilder<SFile> builder = container.create().arrayBuilder(SFile.class);

    for (SFile file : files) {
      if (filter.test(path(file.path().value()))) {
        builder.add(file);
      }
    }

    return builder.build();
  }

  private static Predicate<Path> createFilter(String pattern) {
    try {
      return pathMatcher(pattern);
    } catch (IllegalPathPatternException e) {
      throw new ErrorMessage("Parameter 'include' has illegal value. " + e.getMessage());
    }
  }
}
