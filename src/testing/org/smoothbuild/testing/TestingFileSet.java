package org.smoothbuild.testing;

import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.internal.StoredFileSet;
import org.smoothbuild.testing.fs.base.TestFileSystem;

public class TestingFileSet extends StoredFileSet {
  private final TestFileSystem fileSystem;

  public TestingFileSet() {
    this(new TestFileSystem());
  }

  public TestingFileSet(TestFileSystem fileSystem) {
    super(fileSystem);
    this.fileSystem = fileSystem;
  }

  @Override
  public TestFileSystem fileSystem() {
    return fileSystem;
  }

  public TestingFile createFile(Path path) {
    return new TestingFile(fileSystem(), path);
  }
}
