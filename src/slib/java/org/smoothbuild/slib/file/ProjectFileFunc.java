package org.smoothbuild.slib.file;

import static org.smoothbuild.install.ProjectPaths.SMOOTH_DIR;
import static org.smoothbuild.slib.file.PathArgValidator.validatedProjectPath;

import java.io.IOException;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.vm.bytecode.expr.value.StringB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.compute.Container;

public class ProjectFileFunc {
  public static ValueB func(Container container, TupleB args) throws IOException {
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
