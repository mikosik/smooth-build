package org.smoothbuild.testing.io.fs.base;

import static org.smoothbuild.testing.common.StreamTester.assertContent;

import java.io.IOException;
import java.io.InputStream;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.SubFileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;

public class FakeFileSystem extends SubFileSystem {

  public FakeFileSystem() {
    this(new MemoryFileSystem(), Path.rootPath());
  }

  public FakeFileSystem(FileSystem fileSystem, Path root) {
    super(fileSystem, root);
  }

  public void assertFileContains(Path path, String content) throws IOException {
    InputStream inputStream = openInputStream(path);
    assertContent(inputStream, content);
  }
}
