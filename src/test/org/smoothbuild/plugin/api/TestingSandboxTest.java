package org.smoothbuild.plugin.api;

import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;

import org.junit.Test;
import org.smoothbuild.plugin.TestingSandbox;

public class TestingSandboxTest {
  Path file = path("my/path");
  Path file2 = path("my/path2");

  TestingSandbox testingSandbox = new TestingSandbox();

  @Test
  public void createFileCreatesFileOnFileSystem() throws Exception {
    MutableFile newFile = testingSandbox.createFile(file);
    writeAndClose(newFile.openOutputStream(), file.value());

    testingSandbox.projectFileSystem().assertFileContainsItsPath(file);
  }
}
