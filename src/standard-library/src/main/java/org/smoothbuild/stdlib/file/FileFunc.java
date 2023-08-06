package org.smoothbuild.stdlib.file;

import static org.smoothbuild.common.filesystem.base.PathS.path;
import static org.smoothbuild.stdlib.file.PathArgValidator.validatedProjectPath;

import java.io.IOException;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;

public class FileFunc {
  private static final PathS SMOOTH_DIR = path(".smooth");

  public static ValueB func(Container container, TupleB args)
      throws IOException, BytecodeException {
    StringB path = (StringB) args.get(0);
    PathS validatedPath = validatedProjectPath(container, "path", path);
    if (validatedPath == null) {
      return null;
    }
    if (!validatedPath.isRoot() && validatedPath.firstPart().equals(SMOOTH_DIR)) {
      container.log().error("Reading file from '.smooth' dir is not allowed.");
      return null;
    }

    FileSystem fileSystem = container.fileSystem();
    return switch (fileSystem.pathState(validatedPath)) {
      case FILE -> {
        FileReader reader = new FileReader(container);
        yield reader.createFile(validatedPath, validatedPath);
      }
      case DIR -> {
        container.log().error("File " + validatedPath.q() + " doesn't exist. It is a dir.");
        yield null;
      }
      case NOTHING -> {
        container.log().error("File " + validatedPath.q() + " doesn't exist.");
        yield null;
      }
    };
  }
}
