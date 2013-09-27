package org.smoothbuild.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;
import static org.smoothbuild.testing.plugin.internal.FileTester.createContentWithFilePath;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.smoothbuild.message.Error;
import org.smoothbuild.message.MessageListener;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.fs.base.TestFileSystem;

public class SandboxImplTest {
  Path root = path("my/root");
  Path path1 = path("my/path/file1.txt");
  Path path2 = path("my/path/file2.txt");

  TestFileSystem fileSystem = new TestFileSystem();

  SandboxImpl sandbox = new SandboxImpl(fileSystem, root);

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
  public void reportedErrors() throws Exception {
    Error error = new Error("message");
    MessageListener listener = Mockito.mock(MessageListener.class);
    // MessageListener listener = new PrintingMessageListener();
    sandbox.report(error);
    sandbox.reportCollectedMessagesTo("taskName", listener);

    InOrder inOrder = inOrder(listener);
    inOrder.verify(listener).report(isA(TaskFailedError.class));
    inOrder.verify(listener).report(error);
    verifyNoMoreInteractions(listener);
  }
}
