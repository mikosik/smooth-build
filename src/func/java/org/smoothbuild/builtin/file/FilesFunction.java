package org.smoothbuild.builtin.file;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;
import static org.smoothbuild.lang.message.MessageType.ERROR;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemException;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.task.exec.ContainerImpl;

public class FilesFunction {
  @SmoothFunction
  @NotCacheable
  public static Array<SFile> files(
      ContainerImpl container,
      @Required @Name("dir") SString dir) {
    Path path = validatedPath("dir", dir);
    FileSystem fileSystem = container.projectFileSystem();

    if (path.isRoot()) {
      throw new Message(ERROR, "Listing files from project root is not allowed.");
    }

    if (path.firstPart().equals(SMOOTH_DIR)) {
      throw new Message(ERROR, "Listing files from '.smooth' dir is not allowed.");
    }

    switch (fileSystem.pathState(path)) {
      case DIR:
        return readFiles(container, fileSystem, path);
      case FILE:
        throw new Message(ERROR, "Dir " + path + " doesn't exist. It is a file.");
      case NOTHING:
        throw new Message(ERROR, "Dir " + path + " doesn't exist.");
      default:
        throw new FileSystemException("Broken 'files' function implementation: unreachable case");
    }
  }

  private static Array<SFile> readFiles(ContainerImpl container, FileSystem fileSystem, Path path) {
    ArrayBuilder<SFile> fileArrayBuilder = container.create().arrayBuilder(SFile.class);
    FileReader reader = new FileReader(container);
    for (Path filePath : fileSystem.filesFromRecursive(path)) {
      fileArrayBuilder.add(reader.createFile(filePath, path.append(filePath)));
    }
    return fileArrayBuilder.build();
  }
}
