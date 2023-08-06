package org.smoothbuild.stdlib.file;

import static org.smoothbuild.common.filesystem.base.PathS.path;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.filePath;

import java.util.function.Predicate;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.stdlib.file.match.IllegalPathPatternException;
import org.smoothbuild.stdlib.file.match.PathMatcher;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayBBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class FilterFilesFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    ArrayB files = (ArrayB) args.get(0);
    StringB pattern = (StringB) args.get(1);

    Predicate<PathS> filter;
    try {
      filter = new PathMatcher(pattern.toJ());
    } catch (IllegalPathPatternException e) {
      nativeApi.log().error("Parameter 'pattern' has illegal value. " + e.getMessage());
      return null;
    }
    ArrayBBuilder builder =
        nativeApi.factory().arrayBuilderWithElems(nativeApi.factory().fileT());

    for (TupleB file : files.elems(TupleB.class)) {
      if (filter.test(path(filePath(file).toJ()))) {
        builder.add(file);
      }
    }

    return builder.build();
  }
}
