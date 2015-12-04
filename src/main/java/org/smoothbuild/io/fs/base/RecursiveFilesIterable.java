package org.smoothbuild.io.fs.base;

import static com.google.common.base.Preconditions.checkState;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;

import java.util.ArrayDeque;
import java.util.Iterator;

import com.google.common.collect.ImmutableList;

public class RecursiveFilesIterable implements Iterable<Path> {
  private final FileSystem fileSystem;

  public static Iterable<Path> recursiveFilesIterable(FileSystem fileSystem, Path dir) {
    switch (fileSystem.pathState(dir)) {
      case FILE:
        throw new IllegalArgumentException("Path " + dir + " is not a dir but a file.");
      case DIR:
        return new RecursiveFilesIterable(new SubFileSystem(fileSystem, dir));
      case NOTHING:
        return ImmutableList.of();
      default:
        throw new RuntimeException("Unexpected case: " + fileSystem.pathState(dir));
    }
  }

  private RecursiveFilesIterable(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Override
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
      this.dirStack.push(Path.root());
      this.nextFile = fetchNextFile();
    }

    @Override
    public boolean hasNext() {
      return nextFile != null;
    }

    @Override
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
          for (Path name : fileSystem.files(dir)) {
            fileStack.push(dir.append(name));
          }
        } else {
          Path file = fileStack.remove();
          switch (fileSystem.pathState(file)) {
            case FILE:
              return file;
            case DIR:
              dirStack.push(file);
              break;
            case NOTHING:
              throw new RuntimeException("Unexpected case: " + NOTHING);
            default:
              throw new RuntimeException("Unexpected case: " + fileSystem.pathState(file));
          }
        }
      }
      return null;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
