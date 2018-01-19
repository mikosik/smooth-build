package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.match.PathMatcher.pathMatcher;
import static org.smoothbuild.io.fs.base.Path.path;

import java.util.function.Predicate;

import org.smoothbuild.builtin.file.match.IllegalPathPatternException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;

public class FilterFunction {
  @SmoothFunction
  public static Array filter(NativeApi nativeApi, Array files, SString include) {
    Predicate<Path> filter = null;
    try {
      filter = pathMatcher(include.data());
    } catch (IllegalPathPatternException e) {
      nativeApi.log().error("Parameter 'include' has illegal value. " + e.getMessage());
      return null;
    }
    ArrayBuilder builder = nativeApi.create().arrayBuilder(nativeApi.types().file());

    for (Struct file : files.asIterable(Struct.class)) {
      if (filter.test(path(((SString) file.get("path")).data()))) {
        builder.add(file);
      }
    }

    return builder.build();
  }
}
