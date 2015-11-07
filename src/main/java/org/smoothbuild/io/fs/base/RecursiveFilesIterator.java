package org.smoothbuild.io.fs.base;

import static com.google.common.base.Preconditions.checkState;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;

import java.util.ArrayDeque;
import java.util.Iterator;

public class RecursiveFilesIterator implements Iterator<Path> {
  private final FileSystem fileSystem;

  private final ArrayDeque<Path> directoryStack;
  private final ArrayDeque<Path> fileStack;
  private Path nextFile;

  public RecursiveFilesIterator(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
    this.directoryStack = new ArrayDeque<>();
    this.fileStack = new ArrayDeque<>();
    this.directoryStack.push(Path.root());

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
        for (Path name : fileSystem.filesFrom(dir)) {
          fileStack.push(dir.append(name));
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
