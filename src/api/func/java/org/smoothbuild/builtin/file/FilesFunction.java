package org.smoothbuild.builtin.file;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.builtin.file.PathArgValidator.validatedProjectPath;
import static org.smoothbuild.io.fs.base.RecursiveFilesIterable.recursiveFilesIterable;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.task.exec.Container;

public class FilesFunction {
  @SmoothFunction
  @NotCacheable
  public static Array files(Container container, SString dir) {
    Path path = validatedProjectPath(container, "dir", dir);
    FileSystem fileSystem = container.fileSystem();

    if (path.startsWith(SMOOTH_DIR)) {
      container.log().error("Listing files from " + SMOOTH_DIR + " dir is not allowed.");
      return null;
    }

    switch (fileSystem.pathState(path)) {
      case DIR:
        return readFiles(container, fileSystem, path);
      case FILE:
        container.log().error("Dir " + path + " doesn't exist. It is a file.");
        return null;
      case NOTHING:
        container.log().error("Dir " + path + " doesn't exist.");
        return null;
      default:
        throw new RuntimeException("Broken 'files' function implementation: unreachable case");
    }
  }

  private static Array readFiles(Container container, FileSystem fileSystem, Path dir) {
    ArrayBuilder fileArrayBuilder = container.create().arrayBuilder(container.types().file());
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
