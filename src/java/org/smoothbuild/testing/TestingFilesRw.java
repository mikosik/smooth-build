package org.smoothbuild.testing;

import org.smoothbuild.fs.mem.InMemoryFileSystem;
import org.smoothbuild.lang.internal.FilesRwImpl;
import org.smoothbuild.lang.type.Path;

public class TestingFilesRw extends FilesRwImpl {
  public TestingFilesRw() {
    super(new InMemoryFileSystem(), Path.rootPath());
  }

  @Override
  public TestingFileRw createFileRw(Path path) {
    return new TestingFileRw(fileSystem(), root(), path);
  }
}
