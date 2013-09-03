package org.smoothbuild.testing;

import org.smoothbuild.fs.plugin.FileSetImpl;
import org.smoothbuild.plugin.Path;

public class TestingFileSet extends FileSetImpl {
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
