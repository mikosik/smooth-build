package org.smoothbuild.testing.plugin.internal;

import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;

import org.junit.Test;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.plugin.internal.TestSandbox;

public class TestSandboxTest {
  Path file = path("my/path");
  Path file2 = path("my/path2");

  TestSandbox testSandbox = new TestSandbox();

  @Test
  public void createFileCreatesFileOnFileSystem() throws Exception {
    MutableFile newFile = testSandbox.createFile(file);
    writeAndClose(newFile.openOutputStream(), file.value());

    testSandbox.projectFileSystem().assertFileContains(TestSandbox.SANDBOX_ROOT.append(file),
        file.value());
  }
}
