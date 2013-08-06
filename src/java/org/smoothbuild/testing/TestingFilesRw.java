package org.smoothbuild.testing;

import static org.smoothbuild.lang.type.Path.projectRootPath;

import org.smoothbuild.fs.mem.InMemoryFileSystem;
import org.smoothbuild.lang.internal.FilesRwImpl;
import org.smoothbuild.lang.type.Path;

public class TestingFilesRw extends FilesRwImpl {
  public TestingFilesRw() {
    super(new InMemoryFileSystem(), projectRootPath());
  }

  @Override
  public TestingFileRw createFileRw(Path path) {
    return new TestingFileRw(fileSystem(), root(), path);
  }
}
