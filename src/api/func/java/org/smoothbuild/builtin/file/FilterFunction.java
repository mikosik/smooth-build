package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.match.PathMatcher.pathMatcher;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.message.MessageException.errorException;
import static org.smoothbuild.lang.type.Types.FILE;

import java.util.function.Predicate;

import org.smoothbuild.builtin.file.match.IllegalPathPatternException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;

public class FilterFunction {
  @SmoothFunction
  public static Array filter(Container container, Array files, SString include) {
    Predicate<Path> filter = createFilter(include.value());
    ArrayBuilder builder = container.create().arrayBuilder(FILE);

    for (Value fileValue : files) {
      SFile file = (SFile) fileValue;
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
      throw errorException("Parameter 'include' has illegal value. " + e.getMessage());
    }
  }
}
