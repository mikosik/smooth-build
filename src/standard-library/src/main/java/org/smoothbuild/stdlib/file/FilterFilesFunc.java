package org.smoothbuild.stdlib.file;

import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.filePath;

import java.util.function.Predicate;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.stdlib.file.match.IllegalPathPatternException;
import org.smoothbuild.stdlib.file.match.PathMatcher;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArrayBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class FilterFilesFunc {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    BArray files = (BArray) args.get(0);
    BString pattern = (BString) args.get(1);

    Predicate<Path> filter;
    try {
      filter = new PathMatcher(pattern.toJavaString());
    } catch (IllegalPathPatternException e) {
      nativeApi.log().error("Parameter 'pattern' has illegal value. " + e.getMessage());
      return null;
    }
    BArrayBuilder builder =
        nativeApi.factory().arrayBuilderWithElements(nativeApi.factory().fileType());

    for (BTuple file : files.elements(BTuple.class)) {
      if (filter.test(path(filePath(file).toJavaString()))) {
        builder.add(file);
      }
    }

    return builder.build();
  }
}
