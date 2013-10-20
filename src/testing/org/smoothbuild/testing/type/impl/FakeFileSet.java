package org.smoothbuild.testing.type.impl;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.type.impl.MutableStoredFileSet;

public class FakeFileSet extends MutableStoredFileSet {
  private final FakeFileSystem fileSystem;

  public FakeFileSet() {
    this(new FakeFileSystem());
  }

  public FakeFileSet(FakeFileSystem fileSystem) {
    super(fileSystem);
    this.fileSystem = fileSystem;
  }

  public FakeFile file(Path path) {
    return new FakeFile(fileSystem, path);
  }

  @Override
  public FakeFileSystem fileSystem() {
    return fileSystem;
  }

  @Override
  public FakeFile createFile(Path path) {
    return new FakeFile(fileSystem, path);
  }
}
