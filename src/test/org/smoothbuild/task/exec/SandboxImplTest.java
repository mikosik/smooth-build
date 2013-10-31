package org.smoothbuild.task.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.util.Streams.inputStreamToString;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileBuilder;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.FileSetBuilder;
import org.smoothbuild.task.base.LocatedTask;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.db.FakeObjectDb;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.plugin.FileSetMatchers;

import com.google.common.collect.Iterables;
import com.google.common.hash.HashCode;

public class SandboxImplTest {
  String content = "content";
  Path path1 = path("my/path/file1.txt");
  Path path2 = path("my/path/file2.txt");
  CodeLocation codeLocation = new FakeCodeLocation();
  Task task = mock(Task.class);

  FakeFileSystem fileSystem = new FakeFileSystem();
  FakeObjectDb objectDb = new FakeObjectDb(fileSystem);

  SandboxImpl sandbox = new SandboxImpl(fileSystem, objectDb, new LocatedTask(task, codeLocation));

  @Test
  public void file_builder_stores_file_in_object_db() throws Exception {
    FileBuilder fileBuilder = sandbox.fileBuilder();
    fileBuilder.setPath(path1);
    StreamTester.writeAndClose(fileBuilder.openOutputStream(), content);
    HashCode hash = fileBuilder.build().hash();

    File file = objectDb.file(hash);
    assertThat(file.path()).isEqualTo(path1);
    assertThat(inputStreamToString(file.openInputStream())).isEqualTo(content);
  }

  @Test
  public void file_set_builder_stores_files_in_object_db() throws Exception {
    FileBuilder fileBuilder = sandbox.fileBuilder();
    fileBuilder.setPath(path1);
    StreamTester.writeAndClose(fileBuilder.openOutputStream(), content);
    File file = fileBuilder.build();

    FileSetBuilder builder = sandbox.fileSetBuilder();
    builder.add(file);
    HashCode hash = builder.build().hash();

    FileSet fileSet = objectDb.fileSet(hash);
    MatcherAssert.assertThat(fileSet, FileSetMatchers.containsFileContaining(path1, content));
    assertThat(Iterables.size(fileSet)).isEqualTo(1);
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
