package org.smoothbuild.type.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.fs.base.PathState.NOTHING;
import static org.smoothbuild.testing.type.impl.FileTester.createContentWithFilePath;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.fs.base.FakeFileSystem;

public class MutableStoredFileSetTest {
  Path path = path("my/file");
  String content = "content";

  FakeFileSystem fileSystem = new FakeFileSystem();
  MutableStoredFileSet fileSet = new MutableStoredFileSet(fileSystem);

  @Test
  public void createFile() throws IOException {
    createContentWithFilePath(fileSet.createFile(path));
    fileSystem.assertFileContainsItsPath(path);
  }

  @Test
  public void openFileOutputStream() throws IOException {
    StreamTester.writeAndClose(fileSet.openFileOutputStream(path), content);
    fileSystem.assertFileContains(path, content);
  }

  // TODO uncomment when refactoring ends or add check in (to be created)
  // FileSetBuilder that all opened OutputStreams have been closed.
  // @Test
  public void file_is_not_saved_when_output_stream_is_not_closed() throws IOException {
    OutputStream outputStream = fileSet.openFileOutputStream(path);
    outputStream.write("abc".getBytes());
    assertThat(fileSystem.pathState(path)).isEqualTo(NOTHING);
  }
}
