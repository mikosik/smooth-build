package org.smoothbuild.stdlib.file;

import static java.util.Objects.requireNonNullElse;
import static org.smoothbuild.stdlib.file.PathArgValidator.validatedProjectPath;

import java.io.IOException;
import org.jspecify.annotations.Nullable;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;

public class FilesFunc {
  @Nullable
  public static BValue func(Container container, BTuple args) throws BytecodeException {
    BString dir = (BString) args.get(0);
    Path path = validatedProjectPath(container, "dir", dir);
    if (path == null) {
      return null;
    }
    try {
      return container.bFileCreator().createFiles(path);
    } catch (IOException e) {
      container.log().error(requireNonNullElse(e.getMessage(), "IOException without message"));
      return null;
    }
  }
}
