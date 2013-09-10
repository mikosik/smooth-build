package org.smoothbuild.fs.base;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Iterator;

import org.smoothbuild.plugin.api.Path;

import com.google.common.collect.ImmutableList;

public class RecursiveFilesIterable implements Iterable<Path> {
  private final FileSystem fileSystem;
  private final Path directory;

  public static Iterable<Path> recursiveFilesIterable(FileSystem fileSystem, Path directory) {
    if (fileSystem.pathExistsAndIsDirectory(directory)) {
      return new RecursiveFilesIterable(fileSystem, directory);
    } else if (fileSystem.pathExists(directory)) {
      throw new IllegalArgumentException("Path " + directory + " is not a dir but a file.");
    } else {
      return ImmutableList.of();
    }
  }

  private RecursiveFilesIterable(FileSystem fileSystem, Path directory) {
    checkArgument(fileSystem.pathExistsAndIsDirectory(directory));
    this.fileSystem = fileSystem;
    this.directory = directory;
  }

  @Override
  public Iterator<Path> iterator() {
    return new RecursiveFilesIterator(fileSystem, directory);
  }
}
