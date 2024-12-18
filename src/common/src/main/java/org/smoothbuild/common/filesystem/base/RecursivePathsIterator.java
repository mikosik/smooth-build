package org.smoothbuild.common.filesystem.base;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class RecursivePathsIterator implements PathIterator {
  private final FileSystem<FullPath> fileSystem;
  private final FullPath baseDir;
  private final Deque<Path> dirStack;
  private final Deque<Path> pathStack;
  private Path nextFile;
  private boolean initialized;

  public RecursivePathsIterator(FileSystem<FullPath> fileSystem, FullPath baseDir) {
    this.fileSystem = fileSystem;
    this.baseDir = baseDir;
    this.dirStack = new ArrayDeque<>();
    this.pathStack = new ArrayDeque<>();
    this.dirStack.push(Path.root());
    this.nextFile = null;
  }

  @Override
  public boolean hasNext() throws IOException {
    initializeIfNotInitialized();
    return nextFile != null;
  }

  private void initializeIfNotInitialized() throws IOException {
    if (!initialized) {
      nextFile = fetchNextFile();
      initialized = true;
    }
  }

  @Override
  public Path next() throws IOException {
    checkState(hasNext());
    Path result = nextFile;
    nextFile = fetchNextFile();
    return result;
  }

  private Path fetchNextFile() throws IOException {
    while (!pathStack.isEmpty() || !dirStack.isEmpty()) {
      if (pathStack.isEmpty()) {
        var path = dirStack.remove();
        fileSystem.files(baseDir.append(path)).forEach(name -> pathStack.push(path.append(name)));
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
