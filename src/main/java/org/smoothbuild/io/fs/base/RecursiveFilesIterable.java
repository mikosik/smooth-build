package org.smoothbuild.io.fs.base;

import java.util.Iterator;

import com.google.common.collect.ImmutableList;

public class RecursiveFilesIterable implements Iterable<Path> {
  private final FileSystem fileSystem;

  public static Iterable<Path> recursiveFilesIterable(FileSystem fileSystem, Path directory) {
    switch (fileSystem.pathState(directory)) {
      case FILE:
        throw new IllegalArgumentException("Path " + directory + " is not a dir but a file.");
      case DIR:
        return new RecursiveFilesIterable(new SubFileSystem(fileSystem, directory));
      case NOTHING:
        return ImmutableList.of();
      default:
        throw new RuntimeException("Unexpected case: " + fileSystem.pathState(directory));
    }
  }

  private RecursiveFilesIterable(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Override
  public Iterator<Path> iterator() {
    return new RecursiveFilesIterator(fileSystem);
  }
}
