package org.smoothbuild.testing;

import org.smoothbuild.fs.plugin.StoredFileSet;
import org.smoothbuild.plugin.Path;

public class TestingFileSet extends StoredFileSet {
  private final TestingFileSystem fileSystem;

  public TestingFileSet() {
    this(new TestingFileSystem());
  }

  public TestingFileSet(TestingFileSystem fileSystem) {
    super(fileSystem, Path.rootPath());
    this.fileSystem = fileSystem;
  }

  @Override
  public TestingFileSystem fileSystem() {
    return fileSystem;
  }

  @Override
  public TestingFile createFile(Path path) {
    return new TestingFile(fileSystem(), root(), path);
  }
}
