package org.smoothbuild.task.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CallLocation.callLocation;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;
import static org.smoothbuild.testing.type.impl.FileTester.createContentWithFilePath;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.type.api.MutableFile;

public class SandboxImplTest {
  Path root = path("my/root");
  Path path1 = path("my/path/file1.txt");
  Path path2 = path("my/path/file2.txt");
  CallLocation callLocation = callLocation(simpleName("name"), codeLocation(1, 2, 4));

  TestFileSystem fileSystem = new TestFileSystem();

  SandboxImpl sandbox = new SandboxImpl(fileSystem, root, callLocation);

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
    Message errorMessage = new Message(ERROR, "message");
    sandbox.report(errorMessage);
    assertThat(sandbox.messageGroup()).containsOnly(errorMessage);
  }
}
