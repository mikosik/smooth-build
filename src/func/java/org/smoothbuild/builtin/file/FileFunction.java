package org.smoothbuild.builtin.file;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.builtin.file.PathArgValidator.validatedProjectPath;
import static org.smoothbuild.lang.message.MessageType.ERROR;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemException;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.task.exec.ContainerImpl;

public class FileFunction {
  @SmoothFunction
  @NotCacheable
  public static SFile file(
      ContainerImpl container,
      @Required @Name("path") SString pathString) {
    Path path = validatedProjectPath("path", pathString);
    if (!path.isRoot() && path.firstPart().equals(SMOOTH_DIR)) {
      throw new Message(ERROR, "Reading file from '.smooth' dir is not allowed.");
    }

    FileSystem fileSystem = container.projectFileSystem();
    switch (fileSystem.pathState(path)) {
      case FILE:
        FileReader reader = new FileReader(container);
        return reader.createFile(path, path);
      case DIR:
        throw new Message(ERROR, "File " + path + " doesn't exist. It is a dir.");
      case NOTHING:
        throw new Message(ERROR, "File " + path + " doesn't exist.");
      default:
        throw new FileSystemException("Broken 'file' function implementation: unreachable case");
    }
  }
}
