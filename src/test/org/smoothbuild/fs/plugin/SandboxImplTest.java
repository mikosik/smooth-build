package org.smoothbuild.fs.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.testing.TestingStream.writeAndClose;

import org.junit.Test;
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
  public void createFileCreatesFileOnFileSystem() throws Exception {
    writeAndClose(sandbox.createFile(file).createOutputStream(), file.value());
    fileSystem.assertFileContainsItsPath(root, file);
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
