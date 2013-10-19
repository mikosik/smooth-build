package org.smoothbuild.testing.type.impl;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.type.impl.MutableStoredFileSet;

public class TestFileSet extends MutableStoredFileSet {
  private final TestFileSystem fileSystem;

  public TestFileSet() {
    this(new TestFileSystem());
  }

  public TestFileSet(TestFileSystem fileSystem) {
    super(fileSystem);
    this.fileSystem = fileSystem;
  }

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
