package org.smoothbuild.plugin.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;

import org.junit.Test;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.problem.Error;
import org.smoothbuild.problem.ProblemsListener;
import org.smoothbuild.testing.fs.base.TestFileSystem;

public class SandboxImplTest {
  Path root = path("my/root");
  Path file = path("my/path/file.txt");
  Path file2 = path("my/path/file2.txt");

  TestFileSystem fileSystem = new TestFileSystem();
  ProblemsListener problems = mock(ProblemsListener.class);

  SandboxImpl sandbox = new SandboxImpl(fileSystem, root, problems);

  @Test
  public void createFileCreatesFileOnFileSystem() throws Exception {
    MutableFile newFile = sandbox.createFile(file);
    writeAndClose(newFile.openOutputStream(), file.value());

    fileSystem.subFileSystem(root).assertFileContainsItsPath(file);
  }

  @Test
  public void fileSystem() throws Exception {
    assertThat(sandbox.projectFileSystem()).isSameAs(fileSystem);
  }

  @Test
  public void reportError() throws Exception {
    Error error = new Error("message");
    sandbox.report(error);
    verify(problems).report(error);
  }
}
