package org.smoothbuild.builtin.file;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;
import static org.smoothbuild.message.base.MessageType.FATAL;

import org.smoothbuild.builtin.file.err.IllegalReadFromSmoothDirError;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.NoSuchFileButDirException;
import org.smoothbuild.io.fs.base.err.NoSuchFileException;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.exec.ContainerImpl;

public class FileFunction {
  @SmoothFunction
  @NotCacheable
  public static SFile file( //
      ContainerImpl container, //
      @Required @Name("path") SString pathString) {
    Path path = validatedPath("path", pathString);
    if (!path.isRoot() && path.firstPart().equals(SMOOTH_DIR)) {
      throw new IllegalReadFromSmoothDirError(path);
    }

    FileSystem fileSystem = container.projectFileSystem();
    switch (fileSystem.pathState(path)) {
      case FILE:
        FileReader reader = new FileReader(container);
        return reader.createFile(path, path);
      case DIR:
        throw new NoSuchFileButDirException(path);
      case NOTHING:
        throw new NoSuchFileException(path);
      default:
        throw new Message(FATAL, "Broken 'file' function implementation: unreachable case");
    }
  }
}
