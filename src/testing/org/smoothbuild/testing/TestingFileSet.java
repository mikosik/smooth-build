package org.smoothbuild.testing;

import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.internal.StoredFileSet;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.testing.plugin.internal.TestFile;

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

  public TestFile createFile(Path path) {
    return new TestFile(fileSystem(), path);
  }
}
