package org.smoothbuild.task.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CallLocation.callLocation;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.type.api.MutableFile;
import org.smoothbuild.type.impl.FileSetBuilder;

public class SandboxImplTest {
  String content = "content";
  Path root = path("my/root");
  Path path1 = path("my/path/file1.txt");
  Path path2 = path("my/path/file2.txt");
  CallLocation callLocation = callLocation(simpleName("name"), codeLocation(1, 2, 4));

  TestFileSystem fileSystem = new TestFileSystem();

  SandboxImpl sandbox = new SandboxImpl(fileSystem, root, callLocation);

  @Test
  public void fileSetBuilderStoresFilesInSandboxFileSystem() throws Exception {
    FileSetBuilder builder = sandbox.fileSetBuilder();
    StreamTester.writeAndClose(builder.openFileOutputStream(path1), content);
    fileSystem.assertFileContains(root.append(path1), content);
  }

  @Test
  public void createFileCreatesFileOnFileSystem() throws Exception {
    MutableFile newFile = sandbox.createFile(path1);
    writeAndClose(newFile.openOutputStream(), path1.value());

    fileSystem.subFileSystem(root).assertFileContainsItsPath(path1);
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
