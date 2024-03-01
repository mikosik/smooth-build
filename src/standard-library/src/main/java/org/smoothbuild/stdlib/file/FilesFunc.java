package org.smoothbuild.stdlib.file;

import static org.smoothbuild.common.Throwables.unexpectedCaseExc;
import static org.smoothbuild.common.filesystem.base.PathS.path;
import static org.smoothbuild.common.filesystem.base.RecursivePathsIterator.recursivePathsIterator;
import static org.smoothbuild.stdlib.file.PathArgValidator.validatedProjectPath;

import java.io.IOException;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.PathIterator;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.common.filesystem.base.PathState;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;

public class FilesFunc {
  private static final PathS SMOOTH_DIR = path(".smooth");

  public static ValueB func(Container container, TupleB args)
      throws IOException, BytecodeException {
    StringB dir = (StringB) args.get(0);
    PathS path = validatedProjectPath(container, "dir", dir);
    if (path == null) {
      return null;
    }
    FileSystem fileSystem = container.fileSystem();

    if (path.startsWith(SMOOTH_DIR)) {
      container.log().error("Listing files from " + SMOOTH_DIR.q() + " dir is not allowed.");
      return null;
    }

    return switch (fileSystem.pathState(path)) {
      case DIR -> readFiles(container, fileSystem, path);
      case FILE -> {
        container.log().error("Dir " + path.q() + " doesn't exist. It is a file.");
        yield null;
      }
      case NOTHING -> {
        container.log().error("Dir " + path.q() + " doesn't exist.");
        yield null;
      }
    };
  }

  private static ArrayB readFiles(Container container, FileSystem fileSystem, PathS dir)
      throws IOException, BytecodeException {
    var fileArrayBuilder =
        container.factory().arrayBuilderWithElements(container.factory().fileT());
    var reader = new FileReader(container);
    if (dir.isRoot()) {
      for (PathS path : fileSystem.files(PathS.root())) {
        if (!path.equals(SMOOTH_DIR)) {
          PathState pathState = fileSystem.pathState(path);
          switch (pathState) {
            case DIR:
              for (PathIterator it = recursivePathsIterator(fileSystem, path); it.hasNext(); ) {
                PathS currentPath = it.next();
                PathS projectPath = path.append(currentPath);
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
        PathS path = it.next();
        fileArrayBuilder.add(reader.createFile(path, dir.append(path)));
      }
    }
    return fileArrayBuilder.build();
  }
}
