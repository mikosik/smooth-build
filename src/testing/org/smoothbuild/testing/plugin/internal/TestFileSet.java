package org.smoothbuild.testing.plugin.internal;

import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.internal.MutableStoredFileSet;
import org.smoothbuild.testing.fs.base.TestFileSystem;

public class TestFileSet extends MutableStoredFileSet {
  private final TestFileSystem fileSystem;

  public TestFileSet() {
    this(new TestFileSystem());
  }

  public TestFileSet(TestFileSystem fileSystem) {
    super(fileSystem);
    this.fileSystem = fileSystem;
  }

  @Override
  public TestFile file(Path path) {
    return new TestFile(fileSystem, path);
  }

  @Override
  public TestFileSystem fileSystem() {
    return fileSystem;
  }

  @Override
  public TestFile createFile(Path path) {
    return new TestFile(fileSystem, path);
  }
}
