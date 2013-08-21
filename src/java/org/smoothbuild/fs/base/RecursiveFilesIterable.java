package org.smoothbuild.fs.base;

import java.util.Iterator;

import org.smoothbuild.plugin.Path;

public class RecursiveFilesIterable implements Iterable<Path> {
  private final FileSystem fileSystem;
  private final Path directory;

  public RecursiveFilesIterable(FileSystem fileSystem, Path directory) {
    this.fileSystem = fileSystem;
    this.directory = directory;
  }

  @Override
  public Iterator<Path> iterator() {
    return new RecursiveFilesIterator(fileSystem, directory);
  }
}
