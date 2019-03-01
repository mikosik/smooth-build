package org.smoothbuild.io.fs.base;

import static com.google.common.base.Preconditions.checkState;
import static org.smoothbuild.io.fs.base.Path.root;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;

public class RecursivePathsIterator implements PathIterator {
  private final FileSystem fileSystem;
  private final Path baseDir;
  private final ArrayDeque<Path> dirStack;
  private final ArrayDeque<Path> fileStack;
  private Path nextFile;

  public static PathIterator recursivePathsIterator(FileSystem fileSystem, Path dir)
      throws IOException {
    PathState state = fileSystem.pathState(dir);
    switch (state) {
      case FILE:
        throw new IllegalArgumentException("Path " + dir + " is not a dir but a file.");
      case DIR:
        return new RecursivePathsIterator(fileSystem, dir);
      case NOTHING:
        return new PathIterator() {
          @Override
          public boolean hasNext() {
            return false;
          }

          @Override
          public Path next() {
            throw new NoSuchElementException();
          }
        };
      default:
        throw new RuntimeException("Unexpected case: " + state);
    }
  }

  public RecursivePathsIterator(FileSystem fileSystem, Path baseDir) throws IOException {
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
  public Path next() throws IOException {
    checkState(hasNext());
    Path result = nextFile;
    nextFile = fetchNextFile();
    return result;
  }

  private Path fetchNextFile() throws IOException {
    while (!fileStack.isEmpty() || !dirStack.isEmpty()) {
      if (fileStack.isEmpty()) {
        Path dir = dirStack.remove();
        for (Path name : fileSystem.files(baseDir.append(dir))) {
          fileStack.push(dir.append(name));
        }
      } else {
        Path file = fileStack.remove();
        switch (fileSystem.pathState(baseDir.append(file))) {
          case FILE:
            return file;
          case DIR:
            dirStack.push(file);
            break;
          case NOTHING:
            throw new RuntimeException("Unexpected case: " + NOTHING);
          default:
            throw new RuntimeException(
                "Unexpected case: " + fileSystem.pathState(baseDir.append(file)));
        }
      }
    }
    return null;
  }
}
