package org.smoothbuild.testing.task.exec;

import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.type.api.MutableFile;

public class FakeSandboxTest {
  Path path1 = path("my/path1");
  Path path2 = path("my/path2");

  FakeSandbox fakeSandbox = new FakeSandbox();

  @Test
  public void createFileCreatesFileOnFileSystem() throws Exception {
    MutableFile newFile = fakeSandbox.createFile(path1);
    writeAndClose(newFile.openOutputStream(), path1.value());

    Path fullPath = FakeSandbox.SANDBOX_ROOT.append(path1);
    fakeSandbox.projectFileSystem().assertFileContains(fullPath, path1.value());
  }
}
