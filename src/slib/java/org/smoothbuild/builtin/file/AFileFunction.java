package org.smoothbuild.builtin.file;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.builtin.file.PathArgValidator.validatedProjectPath;

import java.io.IOException;

import org.smoothbuild.exec.task.base.Container;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class AFileFunction {
  @SmoothFunction(value = "aFile", cacheable = false)
  public static Struct file(Container container, SString path) throws IOException {
    Path validatedPath = validatedProjectPath(container, "path", path);
    if (!validatedPath.isRoot() && validatedPath.firstPart().equals(SMOOTH_DIR)) {
      container.log().error("Reading file from '.smooth' dir is not allowed.");
      return null;
    }

    FileSystem fileSystem = container.fileSystem();
    switch (fileSystem.pathState(validatedPath)) {
      case FILE:
        FileReader reader = new FileReader(container);
        return reader.createFile(validatedPath, validatedPath);
      case DIR:
        container.log().error("File " + validatedPath + " doesn't exist. It is a dir.");
        return null;
      case NOTHING:
        container.log().error("File " + validatedPath + " doesn't exist.");
        return null;
      default:
        throw new RuntimeException("Broken 'file' function implementation: unreachable case");
    }
  }
}
