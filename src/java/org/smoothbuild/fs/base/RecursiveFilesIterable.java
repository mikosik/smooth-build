package org.smoothbuild.fs.base;

import java.util.Iterator;


public class RecursiveFilesIterable implements Iterable<String> {
  private final FileSystem fileSystem;
  private final String directory;

  public RecursiveFilesIterable(FileSystem fileSystem, String directory) {
    this.fileSystem = fileSystem;
    this.directory = directory;
  }

  @Override
  public Iterator<String> iterator() {
    return new RecursiveFilesIterator(fileSystem, directory);
  }
}
