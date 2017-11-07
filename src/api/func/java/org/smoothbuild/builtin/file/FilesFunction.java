package org.smoothbuild.builtin.file;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.builtin.file.PathArgValidator.validatedProjectPath;
import static org.smoothbuild.io.fs.base.RecursiveFilesIterable.recursiveFilesIterable;
import static org.smoothbuild.lang.message.MessageException.errorException;
import static org.smoothbuild.lang.type.Types.FILE;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.task.exec.ContainerImpl;

public class FilesFunction {
  @SmoothFunction
  @NotCacheable
  public static Array files(ContainerImpl container, SString dir) {
    Path path = validatedProjectPath("dir", dir);
    FileSystem fileSystem = container.fileSystem();

    if (path.startsWith(SMOOTH_DIR)) {
      throw errorException("Listing files from " + SMOOTH_DIR + " dir is not allowed.");
    }

    switch (fileSystem.pathState(path)) {
      case DIR:
        return readFiles(container, fileSystem, path);
      case FILE:
        throw errorException("Dir " + path + " doesn't exist. It is a file.");
      case NOTHING:
        throw errorException("Dir " + path + " doesn't exist.");
      default:
        throw new RuntimeException("Broken 'files' function implementation: unreachable case");
    }
  }

  private static Array readFiles(ContainerImpl container, FileSystem fileSystem, Path dir) {
    ArrayBuilder fileArrayBuilder = container.create().arrayBuilder(FILE);
    FileReader reader = new FileReader(container);
    if (dir.isRoot()) {
      for (Path path : fileSystem.files(Path.root())) {
        if (!path.equals(SMOOTH_DIR)) {
          PathState pathState = fileSystem.pathState(path);
          switch (pathState) {
            case DIR:
              for (Path currentPath : recursiveFilesIterable(fileSystem, path)) {
                Path projectPath = path.append(currentPath);
                fileArrayBuilder.add(reader.createFile(projectPath, projectPath));
              }
              break;
            case FILE:
              fileArrayBuilder.add(reader.createFile(path, path));
              break;
            default:
              throw new RuntimeException("Unexpected case: " + pathState);
          }
        }
      }
    } else {
      for (Path path : recursiveFilesIterable(fileSystem, dir)) {
        fileArrayBuilder.add(reader.createFile(path, dir.append(path)));
      }
    }
    return fileArrayBuilder.build();
  }
}
