package org.smoothbuild.slib.file;

import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.run.eval.FileStruct.filePath;
import static org.smoothbuild.slib.file.match.PathMatcher.pathMatcher;

import java.util.function.Predicate;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.ArrayBBuilder;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.slib.file.match.IllegalPathPatternExc;

public class FilterFunc {
  public static ArrayB func(NativeApi nativeApi, ArrayB files, StringB pattern) {
    Predicate<PathS> filter;
    try {
      filter = pathMatcher(pattern.toJ());
    } catch (IllegalPathPatternExc e) {
      nativeApi.log().error("Parameter 'pattern' has illegal value. " + e.getMessage());
      return null;
    }
    ArrayBBuilder builder = nativeApi.factory().arrayBuilderWithElems(nativeApi.factory().fileT());

    for (TupleB file : files.elems(TupleB.class)) {
      if (filter.test(path(filePath(file).toJ()))) {
        builder.add(file);
      }
    }

    return builder.build();
  }
}
