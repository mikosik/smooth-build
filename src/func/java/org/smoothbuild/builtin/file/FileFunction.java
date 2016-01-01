package org.smoothbuild.builtin.file;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.builtin.file.PathArgValidator.validatedProjectPath;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.task.exec.ContainerImpl;

public class FileFunction {
  @SmoothFunction
  @NotCacheable
  public static SFile file(ContainerImpl container, SString path) {
    Path validatedPath = validatedProjectPath("path", path);
    if (!validatedPath.isRoot() && validatedPath.firstPart().equals(SMOOTH_DIR)) {
      throw new ErrorMessage("Reading file from '.smooth' dir is not allowed.");
    }

    FileSystem fileSystem = container.fileSystem();
    switch (fileSystem.pathState(validatedPath)) {
      case FILE:
        FileReader reader = new FileReader(container);
        return reader.createFile(validatedPath, validatedPath);
      case DIR:
        throw new ErrorMessage("File " + validatedPath + " doesn't exist. It is a dir.");
      case NOTHING:
        throw new ErrorMessage("File " + validatedPath + " doesn't exist.");
      default:
        throw new FileSystemException("Broken 'file' function implementation: unreachable case");
    }
  }
}
