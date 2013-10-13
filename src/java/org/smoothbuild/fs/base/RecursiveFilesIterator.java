package org.smoothbuild.fs.base;

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayDeque;
import java.util.Iterator;


public class RecursiveFilesIterator implements Iterator<Path> {
  private final FileSystem fileSystem;

  private final ArrayDeque<Path> directoryStack;
  private final ArrayDeque<Path> fileStack;
  private Path nextFile;

  public RecursiveFilesIterator(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
    this.directoryStack = new ArrayDeque<Path>();
    this.fileStack = new ArrayDeque<Path>();
    this.directoryStack.push(Path.rootPath());

    nextFile = fetchNextFile();
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
    while (!fileStack.isEmpty() || !directoryStack.isEmpty()) {
      if (fileStack.isEmpty()) {
        Path dir = directoryStack.remove();
        for (String name : fileSystem.childNames(dir)) {
          fileStack.push(dir.append(Path.path(name)));
        }
      } else {
        Path file = fileStack.remove();
        switch (fileSystem.pathState(file)) {
          case FILE:
            return file;
          case DIR:
            directoryStack.push(file);
            break;
          case NOTHING:
            throw new RuntimeException("should not happend");
          default:
            throw new RuntimeException("unreachable case");
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
