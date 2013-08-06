package org.smoothbuild.testing;

import java.io.IOException;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.lang.internal.FileRwImpl;
import org.smoothbuild.lang.type.Path;

public class TestingFileRw extends FileRwImpl {
  public TestingFileRw(FileSystem fileSystem, Path root, Path path) {
    super(fileSystem, root, path);
  }

  public void createTestContent() throws IOException {
    TestingFileContent.writeAndClose(createOutputStream(), path().value());
  }

  public void assertTestContent() throws IOException {
    TestingFileContent.assertFileContent(createInputStream(), path().value());
  }
}
