package org.smoothbuild.slib.file;

import static org.smoothbuild.exec.base.FileStruct.filePath;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.slib.file.match.PathMatcher.pathMatcher;

import java.util.function.Predicate;

import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.ArrayHBuilder;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.slib.file.match.IllegalPathPatternException;

public class FilterFunction {
  public static ArrayH function(NativeApi nativeApi, ArrayH files, StringH pattern) {
    Predicate<Path> filter;
    try {
      filter = pathMatcher(pattern.jValue());
    } catch (IllegalPathPatternException e) {
      nativeApi.log().error("Parameter 'pattern' has illegal value. " + e.getMessage());
      return null;
    }
    ArrayHBuilder builder = nativeApi.factory().arrayBuilder(nativeApi.factory().fileType());

    for (TupleH file : files.elements(TupleH.class)) {
      if (filter.test(path(filePath(file).jValue()))) {
        builder.add(file);
      }
    }

    return builder.build();
  }
}
