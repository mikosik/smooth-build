package org.smoothbuild.testing;

import org.smoothbuild.fs.plugin.StoredFileSet;
import org.smoothbuild.plugin.api.Path;

public class TestingFileSet extends StoredFileSet {
  private final TestingFileSystem fileSystem;

  public TestingFileSet() {
    this(new TestingFileSystem());
  }

  public TestingFileSet(TestingFileSystem fileSystem) {
    super(fileSystem);
    this.fileSystem = fileSystem;
  }

  @Override
  public TestingFileSystem fileSystem() {
    return fileSystem;
  }

  public TestingFile createFile(Path path) {
    return new TestingFile(fileSystem(), path);
  }
}
