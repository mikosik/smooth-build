package org.smoothbuild.fs.base;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static org.smoothbuild.fs.base.PathS.root;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;

public class RecursivePathsIterator implements PathIterator {
  private final FileSystem fileSystem;
  private final PathS baseDir;
  private final ArrayDeque<PathS> dirStack;
  private final ArrayDeque<PathS> fileStack;
  private PathS nextFile;

  public static PathIterator recursivePathsIterator(FileSystem fileSystem, PathS dir)
      throws IOException {
    PathState state = fileSystem.pathState(dir);
    return switch (state) {
      case FILE -> throw new IllegalArgumentException("Path " + dir + " is not a dir but a file.");
      case DIR -> new RecursivePathsIterator(fileSystem, dir);
      case NOTHING -> new PathIterator() {
        @Override
        public boolean hasNext() {
          return false;
        }

        @Override
        public PathS next() {
          throw new NoSuchElementException();
        }
      };
    };
  }

  public RecursivePathsIterator(FileSystem fileSystem, PathS baseDir) throws IOException {
    this.fileSystem = fileSystem;
    this.baseDir = baseDir;
    this.dirStack = new ArrayDeque<>();
    this.fileStack = new ArrayDeque<>();
    this.dirStack.push(root());
    this.nextFile = fetchNextFile();
  }

  @Override
  public boolean hasNext() {
    return nextFile != null;
  }

  @Override
  public PathS next() throws IOException {
    checkState(hasNext());
    PathS result = nextFile;
    nextFile = fetchNextFile();
    return result;
  }

  private PathS fetchNextFile() throws IOException {
    while (!fileStack.isEmpty() || !dirStack.isEmpty()) {
      if (fileStack.isEmpty()) {
        PathS dir = dirStack.remove();
        for (PathS name : fileSystem.files(baseDir.append(dir))) {
          fileStack.push(dir.append(name));
        }
      } else {
        PathS file = fileStack.remove();
        PathState state = fileSystem.pathState(baseDir.append(file));
        switch (state) {
          case FILE:
            return file;
          case DIR:
            dirStack.push(file);
            break;
          case NOTHING:
            throw new IOException(format(
                "FileSystem changed when iterating tree of directory %s. Cannot find %s.",
                baseDir.q(), baseDir.append(file).q()));
          default:
            throw unexpectedCaseExc(state);
        }
      }
    }
    return null;
  }
}
