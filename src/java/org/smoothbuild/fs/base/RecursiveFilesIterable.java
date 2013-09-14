package org.smoothbuild.fs.base;

import java.util.Iterator;

import org.smoothbuild.plugin.api.Path;

import com.google.common.collect.ImmutableList;

public class RecursiveFilesIterable implements Iterable<Path> {
  private final FileSystem fileSystem;

  public static Iterable<Path> recursiveFilesIterable(FileSystem fileSystem, Path directory) {
    if (fileSystem.pathExistsAndIsDirectory(directory)) {
      return new RecursiveFilesIterable(new SubFileSystem(fileSystem, directory));
    } else if (fileSystem.pathExists(directory)) {
      throw new IllegalArgumentException("Path " + directory + " is not a dir but a file.");
    } else {
      return ImmutableList.of();
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
