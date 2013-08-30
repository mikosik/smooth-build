package org.smoothbuild.testing;

import org.smoothbuild.fs.mem.MemoryFileSystem;
import org.smoothbuild.fs.plugin.FileListImpl;
import org.smoothbuild.plugin.Path;

public class TestingFileList extends FileListImpl {
  public TestingFileList() {
    super(new MemoryFileSystem(), Path.rootPath());
  }

  @Override
  public TestingFile createFile(Path path) {
    return new TestingFile(fileSystem(), root(), path);
  }
}
