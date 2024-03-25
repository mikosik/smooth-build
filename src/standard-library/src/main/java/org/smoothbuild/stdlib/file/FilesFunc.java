package org.smoothbuild.stdlib.file;

import static org.smoothbuild.common.base.Throwables.unexpectedCaseExc;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.bucket.base.RecursivePathsIterator.recursivePathsIterator;
import static org.smoothbuild.stdlib.file.PathArgValidator.validatedProjectPath;

import java.io.IOException;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.bucket.base.PathIterator;
import org.smoothbuild.common.bucket.base.PathState;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;

public class FilesFunc {
  private static final Path SMOOTH_DIR = path(".smooth");

  public static BValue func(Container container, BTuple args)
      throws IOException, BytecodeException {
    BString dir = (BString) args.get(0);
    Path path = validatedProjectPath(container, "dir", dir);
    if (path == null) {
      return null;
    }
    Bucket bucket = container.bucket();

    if (path.startsWith(SMOOTH_DIR)) {
      container.log().error("Listing files from " + SMOOTH_DIR.q() + " dir is not allowed.");
      return null;
    }

    return switch (bucket.pathState(path)) {
      case DIR -> readFiles(container, bucket, path);
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

  private static BArray readFiles(Container container, Bucket bucket, Path dir)
      throws IOException, BytecodeException {
    var fileArrayBuilder =
        container.factory().arrayBuilderWithElements(container.factory().fileType());
    var reader = new FileReader(container);
    if (dir.isRoot()) {
      for (Path path : bucket.files(Path.root())) {
        if (!path.equals(SMOOTH_DIR)) {
          PathState pathState = bucket.pathState(path);
          switch (pathState) {
            case DIR:
              for (PathIterator it = recursivePathsIterator(bucket, path); it.hasNext(); ) {
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
      for (PathIterator it = recursivePathsIterator(bucket, dir); it.hasNext(); ) {
        Path path = it.next();
        fileArrayBuilder.add(reader.createFile(path, dir.append(path)));
      }
    }
    return fileArrayBuilder.build();
  }
}
