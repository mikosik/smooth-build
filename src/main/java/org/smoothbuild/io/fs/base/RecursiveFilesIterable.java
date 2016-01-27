package org.smoothbuild.io.fs.base;

import static com.google.common.base.Preconditions.checkState;
import static org.smoothbuild.io.fs.base.Path.root;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;

import java.util.ArrayDeque;
import java.util.Iterator;

import com.google.common.collect.ImmutableList;

public class RecursiveFilesIterable implements Iterable<Path> {
  private final FileSystem fileSystem;
  private final Path rootDir;

  public static Iterable<Path> recursiveFilesIterable(FileSystem fileSystem, Path dir) {
    switch (fileSystem.pathState(dir)) {
      case FILE:
        throw new IllegalArgumentException("Path " + dir + " is not a dir but a file.");
      case DIR:
        return new RecursiveFilesIterable(fileSystem, dir);
      case NOTHING:
        return ImmutableList.of();
      default:
        throw new RuntimeException("Unexpected case: " + fileSystem.pathState(dir));
    }
  }

  private RecursiveFilesIterable(FileSystem fileSystem, Path rootDir) {
    this.fileSystem = fileSystem;
    this.rootDir = rootDir;
  }

  public Iterator<Path> iterator() {
    return new RecursiveFilesIterator();
  }

  private class RecursiveFilesIterator implements Iterator<Path> {
    private final ArrayDeque<Path> dirStack;
    private final ArrayDeque<Path> fileStack;
    private Path nextFile;

    public RecursiveFilesIterator() {
      this.dirStack = new ArrayDeque<>();
      this.fileStack = new ArrayDeque<>();
      this.dirStack.push(root());
      this.nextFile = fetchNextFile();
    }

    public boolean hasNext() {
      return nextFile != null;
    }

    public Path next() {
      checkState(hasNext());
      Path result = nextFile;
      nextFile = fetchNextFile();
      return result;
    }

    private Path fetchNextFile() {
      while (!fileStack.isEmpty() || !dirStack.isEmpty()) {
        if (fileStack.isEmpty()) {
          Path dir = dirStack.remove();
          for (Path name : fileSystem.files(rootDir.append(dir))) {
            fileStack.push(dir.append(name));
          }
        } else {
          Path file = fileStack.remove();
          switch (fileSystem.pathState(rootDir.append(file))) {
            case FILE:
              return file;
            case DIR:
              dirStack.push(file);
              break;
            case NOTHING:
              throw new RuntimeException("Unexpected case: " + NOTHING);
            default:
              throw new RuntimeException(
                  "Unexpected case: " + fileSystem.pathState(rootDir.append(file)));
          }
        }
      }
      return null;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
