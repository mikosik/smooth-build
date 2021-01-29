package org.smoothbuild.slib.file;

import static org.smoothbuild.exec.base.FileStruct.filePath;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.slib.file.match.PathMatcher.pathMatcher;

import java.util.function.Predicate;

import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.ArrayBuilder;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.slib.file.match.IllegalPathPatternException;

public class FilterFunction {
  public static Array function(NativeApi nativeApi, Array files, Str pattern) {
    Predicate<Path> filter;
    try {
      filter = pathMatcher(pattern.jValue());
    } catch (IllegalPathPatternException e) {
      nativeApi.log().error("Parameter 'pattern' has illegal value. " + e.getMessage());
      return null;
    }
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(nativeApi.factory().fileSpec());

    for (Tuple file : files.asIterable(Tuple.class)) {
      if (filter.test(path(filePath(file).jValue()))) {
        builder.add(file);
      }
    }

    return builder.build();
  }
}
