package org.smoothbuild.slib.file;

import static org.smoothbuild.install.ProjectPaths.SMOOTH_DIR;
import static org.smoothbuild.slib.file.PathArgValidator.validatedProjectPath;

import java.io.IOException;

import org.smoothbuild.exec.task.base.Container;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.SString;
import org.smoothbuild.record.base.Tuple;

public class AFileFunction {
  @SmoothFunction(value = "aFile", cacheable = false)
  public static Tuple file(Container container, SString path) throws IOException {
    Path validatedPath = validatedProjectPath(container, "path", path);
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
