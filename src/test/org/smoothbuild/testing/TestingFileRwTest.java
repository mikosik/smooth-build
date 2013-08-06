package org.smoothbuild.testing;

import static org.smoothbuild.testing.TestingFileContent.assertFileContent;
import static org.smoothbuild.testing.TestingFileContent.writeAndClose;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.lang.type.Path;

public class TestingFileRwTest {
  TestingFileSystem fileSystem = new TestingFileSystem();
  Path root = Path.path("root/path");
  Path path = Path.path("file/path");

  TestingFileRw testingFileRw = new TestingFileRw(fileSystem, root, path);

  @Test
  public void createTestContent() throws IOException {
    testingFileRw.createTestContent();
    assertFileContent(testingFileRw.createInputStream(), path.value());
  }

  @Test
  public void assertTestContent() throws Exception {
    writeAndClose(fileSystem.createOutputStream(root.append(path)), path.value());
    testingFileRw.assertTestContent();
  }
}
