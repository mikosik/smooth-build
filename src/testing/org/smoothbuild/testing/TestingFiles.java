package org.smoothbuild.testing;

import org.smoothbuild.fs.mem.MemoryFileSystem;
import org.smoothbuild.fs.plugin.FilesImpl;
import org.smoothbuild.plugin.Path;

public class TestingFiles extends FilesImpl {
  public TestingFiles() {
    super(new MemoryFileSystem(), Path.rootPath());
  }

  @Override
  public TestingFile createFile(Path path) {
    return new TestingFile(fileSystem(), root(), path);
  }
}
