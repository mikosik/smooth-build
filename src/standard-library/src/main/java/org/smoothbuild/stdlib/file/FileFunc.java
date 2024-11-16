package org.smoothbuild.stdlib.file;

import static org.smoothbuild.stdlib.file.PathArgValidator.validatedProjectPath;

import java.io.IOException;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;

public class FileFunc {
  public static BValue func(Container container, BTuple args) throws BytecodeException {
    BString path = (BString) args.get(0);
    Path validatedPath = validatedProjectPath(container, "path", path);
    if (validatedPath == null) {
      return null;
    }

    try {
      return container.bFileCreator().createFile(validatedPath, validatedPath);
    } catch (IOException e) {
      container.log().error(e.getMessage());
      return null;
    }
  }
}
