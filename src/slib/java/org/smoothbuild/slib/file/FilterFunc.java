package org.smoothbuild.slib.file;

import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.run.eval.FileStruct.filePath;
import static org.smoothbuild.slib.file.match.PathMatcher.pathMatcher;

import java.util.function.Predicate;

import org.smoothbuild.bytecode.expr.value.ArrayB;
import org.smoothbuild.bytecode.expr.value.ArrayBBuilder;
import org.smoothbuild.bytecode.expr.value.StringB;
import org.smoothbuild.bytecode.expr.value.TupleB;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.slib.file.match.IllegalPathPatternExc;

public class FilterFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    ArrayB files = (ArrayB) args.get(0);
    StringB pattern = (StringB) args.get(1);

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
