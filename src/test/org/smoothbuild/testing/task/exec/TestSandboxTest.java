package org.smoothbuild.testing.task.exec;

import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.type.api.MutableFile;

public class TestSandboxTest {
  Path path1 = path("my/path1");
  Path path2 = path("my/path2");

  TestSandbox testSandbox = new TestSandbox();

  @Test
  public void createFileCreatesFileOnFileSystem() throws Exception {
    MutableFile newFile = testSandbox.createFile(path1);
    writeAndClose(newFile.openOutputStream(), path1.value());

    Path fullPath = TestSandbox.SANDBOX_ROOT.append(path1);
    testSandbox.projectFileSystem().assertFileContains(fullPath, path1.value());
  }
}
