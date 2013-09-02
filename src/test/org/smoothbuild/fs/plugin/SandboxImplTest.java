package org.smoothbuild.fs.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.testing.TestingStream.writeAndClose;

import org.junit.Test;
import org.smoothbuild.plugin.FileList;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.problem.Error;
import org.smoothbuild.problem.ProblemsListener;
import org.smoothbuild.testing.TestingFileSystem;

public class SandboxImplTest {
  Path root = path("my/root");
  Path file = path("my/path/file.txt");
  Path file2 = path("my/path/file2.txt");

  TestingFileSystem fileSystem = new TestingFileSystem();
  ProblemsListener problems = mock(ProblemsListener.class);

  SandboxImpl sandbox = new SandboxImpl(fileSystem, root, problems);

  @Test
  public void resultFileIsCreatedOnFileSystem() throws Exception {
    writeAndClose(sandbox.resultFile(file).createOutputStream(), file.value());
    fileSystem.assertFileContainsItsPath(root, file);
  }

  @Test
  public void cannotCallResultFileTwice() {
    sandbox.resultFile(file);
    try {
      sandbox.resultFile(file);
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }

  @Test
  public void cannotCallResultFileAfterCallingResultFileList() {
    sandbox.resultFileList();
    try {
      sandbox.resultFile(file);
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }

  @Test
  public void resultFilesAreCreatedOnFileSystem() throws Exception {
    FileList resultFileList = sandbox.resultFileList();
    writeAndClose(resultFileList.createFile(file).createOutputStream(), file.value());
    writeAndClose(resultFileList.createFile(file2).createOutputStream(), file2.value());

    fileSystem.assertFileContainsItsPath(root, file);
    fileSystem.assertFileContainsItsPath(root, file2);
  }

  @Test
  public void cannotCallResultFileListAfterCallingResultFileTwice() {
    sandbox.resultFile(file);
    try {
      sandbox.resultFileList();
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }

  @Test
  public void fileSystem() throws Exception {
    assertThat(sandbox.fileSystem()).isSameAs(fileSystem);
  }

  @Test
  public void reportError() throws Exception {
    Error error = new Error("message");
    sandbox.report(error);
    verify(problems).report(error);
  }
}
