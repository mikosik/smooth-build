package org.smoothbuild.testing.task;

import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;

import org.junit.Test;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.common.StreamTester;

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

  @Test
  public void resultFilesSetCreatesFilesInSandboxFileSystem() throws Exception {
    testSandbox.resultFileSet().createFile(path1).createContentWithFilePath();

    Path fullPath = TestSandbox.SANDBOX_ROOT.append(path1);
    StreamTester.assertContent(testSandbox.projectFileSystem().openInputStream(fullPath),
        path1.value());
  }
}
