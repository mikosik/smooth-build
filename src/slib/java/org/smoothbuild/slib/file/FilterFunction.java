package org.smoothbuild.slib.file;

import static org.smoothbuild.exec.base.FileStruct.filePath;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.slib.file.match.PathMatcher.pathMatcher;

import java.util.function.Predicate;

import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.ArrayBuilder;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Tuple;
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
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(nativeApi.factory().fileType());

    for (Tuple file : files.elements(Tuple.class)) {
      if (filter.test(path(filePath(file).jValue()))) {
        builder.add(file);
      }
    }

    return builder.build();
  }
}
