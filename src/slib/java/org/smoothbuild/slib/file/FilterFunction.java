package org.smoothbuild.slib.file;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.record.db.FileStruct.filePath;
import static org.smoothbuild.slib.file.match.PathMatcher.pathMatcher;

import java.util.function.Predicate;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.ArrayBuilder;
import org.smoothbuild.record.base.SString;
import org.smoothbuild.record.base.Tuple;
import org.smoothbuild.slib.file.match.IllegalPathPatternException;

public class FilterFunction {
  @SmoothFunction("filter")
  public static Array filter(NativeApi nativeApi, Array files, SString pattern) {
    Predicate<Path> filter;
    try {
      filter = pathMatcher(pattern.jValue());
    } catch (IllegalPathPatternException e) {
      nativeApi.log().error("Parameter 'pattern' has illegal value. " + e.getMessage());
      return null;
    }
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(nativeApi.factory().fileType());

    for (Tuple file : files.asIterable(Tuple.class)) {
      if (filter.test(path(filePath(file).jValue()))) {
        builder.add(file);
      }
    }

    return builder.build();
  }
}
