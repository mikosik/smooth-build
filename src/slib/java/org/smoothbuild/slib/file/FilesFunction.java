package org.smoothbuild.slib.file;

import static org.smoothbuild.install.ProjectPaths.SMOOTH_DIR;
import static org.smoothbuild.io.fs.base.RecursivePathsIterator.recursivePathsIterator;
import static org.smoothbuild.slib.file.PathArgValidator.validatedProjectPath;

import java.io.IOException;

import org.smoothbuild.exec.task.base.Container;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathIterator;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.ArrayBuilder;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class FilesFunction {
  @SmoothFunction(value = "files", cacheable = false)
  public static Array files(Container container, SString dir) throws IOException {
    Path path = validatedProjectPath(container, "dir", dir);
    FileSystem fileSystem = container.fileSystem();

    if (path.startsWith(SMOOTH_DIR)) {
      container.log().error("Listing files from " + SMOOTH_DIR.q() + " dir is not allowed.");
      return null;
    }

    switch (fileSystem.pathState(path)) {
      case DIR:
        return readFiles(container, fileSystem, path);
      case FILE:
        container.log().error("Dir " + path.q() + " doesn't exist. It is a file.");
        return null;
      case NOTHING:
        container.log().error("Dir " + path.q() + " doesn't exist.");
        return null;
      default:
        throw new RuntimeException("Broken 'files' function implementation: unreachable case");
    }
  }

  private static Array readFiles(Container container, FileSystem fileSystem, Path dir)
      throws IOException {
    ArrayBuilder fileArrayBuilder = container.factory().arrayBuilder(container.factory().fileType());
    FileReader reader = new FileReader(container);
    if (dir.isRoot()) {
      for (Path path : fileSystem.files(Path.root())) {
        if (!path.equals(SMOOTH_DIR)) {
          PathState pathState = fileSystem.pathState(path);
          switch (pathState) {
            case DIR:
              for (PathIterator it = recursivePathsIterator(fileSystem, path); it.hasNext(); ) {
                Path currentPath = it.next();
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
      for (PathIterator it = recursivePathsIterator(fileSystem, dir); it.hasNext(); ) {
        Path path = it.next();
        fileArrayBuilder.add(reader.createFile(path, dir.append(path)));
      }
    }
    return fileArrayBuilder.build();
  }
}
