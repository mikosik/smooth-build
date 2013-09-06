package org.smoothbuild.plugin.api;

import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.testing.TestingStream.writeAndClose;

import org.junit.Test;
import org.smoothbuild.plugin.TestingSandbox;

public class TestingSandboxTest {
  Path file = path("my/path");
  Path file2 = path("my/path2");

  TestingSandbox testingSandbox = new TestingSandbox();

  @Test
  public void createFileCreatesFileOnFileSystem() throws Exception {
    writeAndClose(testingSandbox.createFile(file).openOutputStream(), file.value());
    testingSandbox.projectFileSystem().assertFileContainsItsPath(Path.rootPath(), file);
  }
}
