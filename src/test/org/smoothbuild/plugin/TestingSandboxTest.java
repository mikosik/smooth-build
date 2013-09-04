package org.smoothbuild.plugin;

import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.testing.TestingStream.writeAndClose;

import org.junit.Test;

public class TestingSandboxTest {
  Path file = path("my/path");
  Path file2 = path("my/path2");

  TestingSandbox testingSandbox = new TestingSandbox();

  @Test
  public void createFileCreatesFileOnFileSystem() throws Exception {
    writeAndClose(testingSandbox.createFile(file).createOutputStream(), file.value());
    testingSandbox.fileSystem().assertFileContainsItsPath(Path.rootPath(), file);
  }
}
