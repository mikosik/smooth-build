package org.smoothbuild.testing.plugin.internal;

import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.internal.StoredFileSet;
import org.smoothbuild.testing.fs.base.TestFileSystem;

public class TestFileSet extends StoredFileSet {
  private final TestFileSystem fileSystem;

  public TestFileSet() {
    this(new TestFileSystem());
  }

  private TestFileSet(TestFileSystem fileSystem) {
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
