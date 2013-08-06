package org.smoothbuild.fs.base;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayDeque;
import java.util.Iterator;

import org.smoothbuild.lang.type.Path;

public class RecursiveFilesIterator implements Iterator<Path> {
  private final FileSystem fileSystem;
  private final Path rootPath;

  private final ArrayDeque<Path> directoryStack;
  private final ArrayDeque<Path> fileStack;
  private Path nextFile;

  public RecursiveFilesIterator(FileSystem fileSystem, Path rootPath) {
    checkArgument(fileSystem.isDirectory(rootPath));

    this.fileSystem = fileSystem;
    this.rootPath = rootPath;
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
        Path dirFullPath = rootPath.append(dir);
        for (String name : fileSystem.childNames(dirFullPath)) {
          fileStack.add(dir.append(Path.path(name)));
        }
      } else {
        Path file = fileStack.remove();
        Path fullPath = rootPath.append(file);
        if (fileSystem.isDirectory(fullPath)) {
          directoryStack.add(file);
        } else {
          return file;
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
