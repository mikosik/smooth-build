package org.smoothbuild.task.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CallLocation.callLocation;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.message.message.MessageType.ERROR;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.object.FileBuilder;
import org.smoothbuild.object.FileSetBuilder;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.object.FakeObjectsDb;
import org.smoothbuild.testing.type.impl.FileSetMatchers;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;

import com.google.common.collect.Iterables;
import com.google.common.hash.HashCode;

public class SandboxImplTest {
  String content = "content";
  Path path1 = path("my/path/file1.txt");
  Path path2 = path("my/path/file2.txt");
  CallLocation callLocation = callLocation(simpleName("name"), codeLocation(1, 2, 4));

  FakeFileSystem fileSystem = new FakeFileSystem();
  FakeObjectsDb objectsDb = new FakeObjectsDb(fileSystem);

  SandboxImpl sandbox = new SandboxImpl(fileSystem, objectsDb, callLocation);

  @Test
  public void file_builder_stores_file_in_object_db() throws Exception {
    FileBuilder fileBuilder = sandbox.fileBuilder();
    fileBuilder.setPath(path1);
    StreamTester.writeAndClose(fileBuilder.openOutputStream(), content);
    HashCode hash = fileBuilder.build().hash();

    File file = objectsDb.file(hash);
    assertThat(file.path()).isEqualTo(path1);
    assertThat(StreamTester.inputStreamToString(file.openInputStream())).isEqualTo(content);
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

    FileSet fileSet = objectsDb.fileSet(hash);
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
