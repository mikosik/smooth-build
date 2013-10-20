package org.smoothbuild.testing.type.impl;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.type.impl.MutableStoredFileSet;

public class FakeFileSet extends MutableStoredFileSet {
  private final TestFileSystem fileSystem;

  public FakeFileSet() {
    this(new TestFileSystem());
  }

  public FakeFileSet(TestFileSystem fileSystem) {
    super(fileSystem);
    this.fileSystem = fileSystem;
  }

  public FakeFile file(Path path) {
    return new FakeFile(fileSystem, path);
  }

  @Override
  public TestFileSystem fileSystem() {
    return fileSystem;
  }

  @Override
  public FakeFile createFile(Path path) {
    return new FakeFile(fileSystem, path);
  }
}
