package org.smoothbuild.testing;

import org.smoothbuild.fs.plugin.FileListImpl;
import org.smoothbuild.plugin.Path;

public class TestingFileList extends FileListImpl {
  private final TestingFileSystem fileSystem;

  public TestingFileList() {
    this(new TestingFileSystem());
  }

  public TestingFileList(TestingFileSystem fileSystem) {
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
