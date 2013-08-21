package org.smoothbuild.testing;

import org.smoothbuild.fs.mem.InMemoryFileSystem;
import org.smoothbuild.lang.internal.FilesImpl;
import org.smoothbuild.plugin.Path;

public class TestingFiles extends FilesImpl {
  public TestingFiles() {
    super(new InMemoryFileSystem(), Path.rootPath());
  }

  @Override
  public TestingFile createFile(Path path) {
    return new TestingFile(fileSystem(), root(), path);
  }
}
