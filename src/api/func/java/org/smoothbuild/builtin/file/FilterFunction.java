package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.match.PathMatcher.pathMatcher;
import static org.smoothbuild.io.fs.base.Path.path;

import java.util.function.Predicate;

import org.smoothbuild.builtin.file.match.IllegalPathPatternException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.ArrayBuilder;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class FilterFunction {
  @SmoothFunction("filter")
  public static Array filter(NativeApi nativeApi, Array files, SString include) {
    Predicate<Path> filter;
    try {
      filter = pathMatcher(include.jValue());
    } catch (IllegalPathPatternException e) {
      nativeApi.log().error("Parameter 'include' has illegal value. " + e.getMessage());
      return null;
    }
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(nativeApi.factory().fileType());

    for (Struct file : files.asIterable(Struct.class)) {
      if (filter.test(path(((SString) file.get("path")).jValue()))) {
        builder.add(file);
      }
    }

    return builder.build();
  }
}
