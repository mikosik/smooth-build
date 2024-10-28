package org.smoothbuild.stdlib.file;

import static org.smoothbuild.stdlib.file.PathArgValidator.validatedProjectPath;

import java.io.IOException;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;

public class FilesFunc {
  public static BValue func(Container container, BTuple args) throws BytecodeException {
    BString dir = (BString) args.get(0);
    Path path = validatedProjectPath(container, "dir", dir);
    if (path == null) {
      return null;
    }
    try {
      return container.fileReader().createFiles(path);
    } catch (IOException e) {
      container.log().error(e.getMessage());
      return null;
    }
  }
}
