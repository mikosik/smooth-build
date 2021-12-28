package org.smoothbuild.slib.file;

import static org.smoothbuild.install.ProjectPaths.SMOOTH_DIR;
import static org.smoothbuild.io.fs.base.RecursivePathsIterator.recursivePathsIterator;
import static org.smoothbuild.slib.file.PathArgValidator.validatedProjectPath;
import static org.smoothbuild.slib.util.Throwables.unexpectedCaseExc;

import java.io.IOException;

import org.smoothbuild.db.bytecode.obj.val.ArrayB;
import org.smoothbuild.db.bytecode.obj.val.StringB;
import org.smoothbuild.exec.compute.Container;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathIterator;
import org.smoothbuild.io.fs.base.PathState;

public class ProjectFilesFunc {
  public static ArrayB func(Container container, StringB dir) throws IOException {
    Path path = validatedProjectPath(container, "dir", dir);
    if (path == null) {
      return null;
    }
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

  private static ArrayB readFiles(Container container, FileSystem fileSystem, Path dir)
      throws IOException {
    var fileArrayBuilder = container.factory().arrayBuilderWithElems(container.factory().fileT());
    var reader = new FileReader(container);
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
              throw unexpectedCaseExc(pathState);
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
