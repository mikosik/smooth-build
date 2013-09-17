package org.smoothbuild.plugin.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;
import static org.smoothbuild.testing.plugin.internal.FileTester.createContentWithFilePath;

import org.junit.Test;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.problem.Error;
import org.smoothbuild.problem.MessageListener;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.fs.base.TestFileSystem;

public class SandboxImplTest {
  Path root = path("my/root");
  Path path1 = path("my/path/file1.txt");
  Path path2 = path("my/path/file2.txt");

  TestFileSystem fileSystem = new TestFileSystem();
  MessageListener messages = mock(MessageListener.class);

  SandboxImpl sandbox = new SandboxImpl(fileSystem, root, messages);

  @Test
  public void createFileCreatesFileOnFileSystem() throws Exception {
    MutableFile newFile = sandbox.createFile(path1);
    writeAndClose(newFile.openOutputStream(), path1.value());

    fileSystem.subFileSystem(root).assertFileContainsItsPath(path1);
  }

  @Test
  public void resultFileSetCreatesFilesOnSandboxFileSystem() throws Exception {
    MutableFile file = sandbox.resultFileSet().createFile(path1);
    createContentWithFilePath(file);

    StreamTester.assertContent(fileSystem.openInputStream(root.append(path1)), path1.value());
  }

  @Test
  public void fileSystem() throws Exception {
    assertThat(sandbox.projectFileSystem()).isSameAs(fileSystem);
  }

  @Test
  public void reportError() throws Exception {
    Error error = new Error("message");
    sandbox.report(error);
    verify(messages).report(error);
  }
}
