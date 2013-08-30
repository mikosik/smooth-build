package org.smoothbuild.fs.base;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Iterator;

import org.smoothbuild.plugin.Path;

public class RecursiveFilesIterable implements Iterable<Path> {
  private final FileSystem fileSystem;
  private final Path directory;

  public RecursiveFilesIterable(FileSystem fileSystem, Path directory) {
    checkArgument(fileSystem.pathExistsAndisDirectory(directory));
    this.fileSystem = fileSystem;
    this.directory = directory;
  }

  @Override
  public Iterator<Path> iterator() {
    return new RecursiveFilesIterator(fileSystem, directory);
  }
}
