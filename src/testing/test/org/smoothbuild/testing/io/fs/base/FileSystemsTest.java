package org.smoothbuild.testing.io.fs.base;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.io.fs.base.FileSystems.fileContent;
import static org.smoothbuild.util.Streams.writeAndClose;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;

public class FileSystemsTest {
  private MemoryFileSystem fileSystem;
  private Path path;

  @Test
  public void file_content() throws Exception {
    given(fileSystem = new MemoryFileSystem());
    given(path = path("some/path"));
    when(writeAndClose(fileSystem.openOutputStream(path), "content"));
    thenEqual(fileContent(fileSystem, path), "content");
  }
}
