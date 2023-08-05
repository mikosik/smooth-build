package org.smoothbuild.common.filesystem.base;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

public class RecursivePathsIterator implements PathIterator {
  private final FileSystem fileSystem;
  private final PathS baseDir;
  private final Deque<PathS> dirStack;
  private final Deque<PathS> pathStack;
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
    this.pathStack = new ArrayDeque<>();
    this.dirStack.push(PathS.root());
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
    while (!pathStack.isEmpty() || !dirStack.isEmpty()) {
      if (pathStack.isEmpty()) {
        var path = dirStack.remove();
        for (PathS name : fileSystem.files(baseDir.append(path))) {
          pathStack.push(path.append(name));
        }
      } else {
        var path = pathStack.remove();
        switch (fileSystem.pathState(baseDir.append(path))) {
          case FILE -> {
            return path;
          }
          case DIR -> dirStack.push(path);
          case NOTHING -> throw new IOException(format(
              "FileSystem changed when iterating tree of directory %s. Cannot find %s.",
              baseDir.q(), baseDir.append(path).q()));
        }
      }
    }
    return null;
  }
}
