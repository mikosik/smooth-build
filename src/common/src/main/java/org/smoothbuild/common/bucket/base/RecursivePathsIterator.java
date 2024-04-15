package org.smoothbuild.common.bucket.base;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class RecursivePathsIterator implements PathIterator {
  private final Bucket bucket;
  private final Path baseDir;
  private final Deque<Path> dirStack;
  private final Deque<Path> pathStack;
  private Path nextFile;

  /**
   * @return PathIterator iterating over all files in given `dir` recursively. Paths returns by
   * iterator are relative to `dir`.
   */
  public static PathIterator recursivePathsIterator(Bucket bucket, Path dir) throws IOException {
    PathState state = bucket.pathState(dir);
    return switch (state) {
      case FILE -> throw new IOException("Path " + dir.q() + " is not a dir but a file.");
      case DIR -> new RecursivePathsIterator(bucket, dir);
      case NOTHING -> throw new IOException("Dir " + dir.q() + " doesn't exist.");
    };
  }

  public RecursivePathsIterator(Bucket bucket, Path baseDir) throws IOException {
    this.bucket = bucket;
    this.baseDir = baseDir;
    this.dirStack = new ArrayDeque<>();
    this.pathStack = new ArrayDeque<>();
    this.dirStack.push(Path.root());
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
    while (!pathStack.isEmpty() || !dirStack.isEmpty()) {
      if (pathStack.isEmpty()) {
        var path = dirStack.remove();
        for (Path name : bucket.files(baseDir.append(path))) {
          pathStack.push(path.append(name));
        }
      } else {
        var path = pathStack.remove();
        switch (bucket.pathState(baseDir.append(path))) {
          case FILE -> {
            return path;
          }
          case DIR -> dirStack.push(path);
          case NOTHING -> throw new IOException(format(
              "Bucket changed when iterating tree of directory %s. Cannot find %s.",
              baseDir.q(), baseDir.append(path).q()));
        }
      }
    }
    return null;
  }
}
